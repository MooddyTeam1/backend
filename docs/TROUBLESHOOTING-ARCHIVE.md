# 트러블슈팅 아카이브 (포트폴리오용)

프로젝트 이슈를 해결할 때마다 이 문서에 누적한다. (`.cursor/rules/troubleshooting.mdc` 포맷 준수)

---

## 1. K6 선착순 부하 중 주문 API `SYS-500` — 낙관적 락 예외 미포착

### 📌 한 줄 요약

`jakarta.persistence.OptimisticLockException`만 재시도 처리하던 코드가 **Spring이 실제로 던지는 `ObjectOptimisticLockingFailureException`** 을 건너뛰어, 동시 주문 시 **약 3% 요청이 500(SYS-500)** 으로 떨어지던 문제를 **`OptimisticLockingFailureException` 단일 캐치**로 정리해 재시도·409 충돌 응답으로 정상화함.

### 🚨 AS-IS & Situation

- k6로 `POST /api/orders` 부하(예: 100 VU, 30s) 시 **HTTP 500**, 응답 본문 `code: SYS-500`.
- 인증(401)은 이미 해결된 상태에서 발생 → **비즈니스/인프라 예외가 아닌 미처리 런타임 예외**로 사용자에게 “서버 오류” 노출.
- 선착순·재고 경합이 큰 시나리오에서 **일부만 실패**하는 패턴 → 트랜잭션/락 이슈 정밀 진단 필요.

### 🔬 Diagnosis & Task (Root Cause)

- `Reward` 엔티티에 `@Version` 기반 **낙관적 락**이 있음. 동일 리워드 재고를 동시에 줄이면 flush/commit 시점에 충돌.
- Hibernate/Spring 조합에서는 충돌 시 흔히 **`org.springframework.orm.ObjectOptimisticLockingFailureException`** 이 발생하며, 이 타입은 **`org.springframework.dao.OptimisticLockingFailureException`의 하위 클래스**이다.
- 기존 `createOrder` 재시도 루프는 **`jakarta.persistence.OptimisticLockException`만** catch → 실제로 던져지는 Spring 예외가 **상위로 전파** → `GlobalExceptionHandler`의 `Exception` 핸들러에서 **SYS-500**으로 포장됨.
- 즉, “낙관적 락 재시도 로직이 있다”고 착각할 수 있으나, **프레임워크가 선택한 예외 타입과 catch 타입이 불일치**하여 재시도가 무력화된 상태였음.

### 🛠️ TO-BE & Action

- **캐치 대상을 Spring 추상 예외인 `OptimisticLockingFailureException`으로 통일**한다. (`ObjectOptimisticLockingFailureException` 등 대부분의 낙관적 락 실패를 포괄)
- `createOrder`의 `while` 루프에서 해당 예외를 잡으면 **백오프 후 재시도**, 최대 횟수 초과 시 **`BUSINESS_CONFLICT`(409)** 로 명시적 실패 처리.
- k6 스크립트는 이미 201/409/400/404를 정상 비즈니스 응답으로 집계하도록 되어 있어, **500 스팸 제거**와 관측 일관성이 맞춰짐.

**핵심 코드 (발췌):**

```java
import org.springframework.dao.OptimisticLockingFailureException;

public OrderDetailResponse createOrder(Long userId, OrderCreateRequest request) {
    int attempt = 0;
    while (attempt < MAX_RETRY_ATTEMPTS) {
        try {
            return createOrderInternal(userId, request);
        } catch (OptimisticLockingFailureException e) {
            attempt++;
            // ... 로그, 최대 시도 시 AppException(BUSINESS_CONFLICT), 아니면 sleep 후 재시도
        }
    }
    throw new AppException(ErrorCode.INTERNAL_ERROR, "주문 처리 중 오류가 발생했습니다.");
}
```

### 📈 Result (정량·품질)

- **Before:** 동시 경합 구간에서 미포착 예외 → **SYS-500 비율 ~3%** 수준(k6 요약 기준), SLA·알람 왜곡.
- **After:** 동일 충돌을 **재시도·409**로 소화 → **500은 비정상 경로로만** 남고, 부하 테스트에서 **주문 성공(201) 비중 증가·실패는 의미 있는 409**로 분리 가능.
- (실측 수치는 환경·VU·재고에 따라 다름 — 재실행 시 `http_req_failed`·`checks_failed`로 확인.)

### STAR (면접용 압축)

| 항목 | 내용 |
|------|------|
| **S** | K6 선착순 부하 중 주문 API가 간헐적 **500(SYS-500)** — 오픈런급 트래픽에서 사용자·모니터링 모두 혼란. |
| **T** | 낙관적 락 충돌을 **재시도·409로 흡수**하고, 프레임워크 예외 계층과 **catch 타입을 일치**시킬 것. |
| **A** | `createOrder`에서 **`OptimisticLockingFailureException`만** 포착해 재시도 루프에 태움(Hibernate가 던지는 `ObjectOptimisticLockingFailureException` 포함). |
| **R** | 미처리 예외로 인한 **가짜 500 제거**, 관측 가능한 **비즈니스 충돌(409)** 로 전환, 부하 시나리오 신뢰도 향상. |

---

<!-- 이후 이슈는 위와 동일한 섹션 구조로 이어서 작성 -->

## 2. Redis 분산 락 이후 다품목 재고 정합성 — Lua 원자 선차감 + 보상 트랜잭션 도입

### 📌 한 줄 요약

Redis 락 기반 직렬화만으로는 다품목 주문의 부분 차감/실패 보상 공백이 남아, `Lua all-or-nothing 선차감 + DB 실패 시 보상(INCRBY)` 구조로 재설계해 고경합 주문에서 재고 정합성과 실패 복구력을 동시에 확보했다.

### 🚨 AS-IS & Situation

- 트래픽 급증(오픈런/이벤트)에서 주문 요청이 짧은 시간에 집중될 때, 락 직렬화 이후에도 **여러 리워드를 한 번에 주문하는 시나리오**에서 부분 실패 복구가 복잡했다.
- DB 저장 예외(결제 연계, 제약조건, 예상치 못한 런타임 예외) 발생 시, 선행 단계의 재고 처리와 DB 커밋 사이에서 **정합성 드리프트** 가능성이 존재했다.
- 결과적으로 사용자에게는 재시도 시점별 재고 체감 불일치, 운영 관점에서는 “실패 원인 대비 재고 값” 검증 코스트가 증가할 수 있는 구조였다.

### 🔬 Diagnosis & Task (Root Cause)

- 분산 락은 임계구역 직렬화에는 효과적이지만, 다품목 재고 연산 자체를 **원자적으로 묶어주지 않는다**.
- 기존 접근은 애플리케이션 레벨 루프/검증 의존도가 높아, “N개 리워드 동시 차감”을 단일 원자 연산으로 보장하기 어렵다.
- 또한 트랜잭션 경계 밖(혹은 경계 사이)에서 예외가 발생하면, DB와 Redis 간 상태를 즉시 재수렴시키는 표준화된 보상 경로가 필요했다.

### 🛠️ TO-BE & Action

- Redis Lua 스크립트로 다중 키를 **전수 검증 후 일괄 차감**하는 all-or-nothing 패턴 도입.
- `OrderRedisFacade`에서 다음 흐름으로 경계 분리:
  1) 주문 전 `reserveStocksForCreate()`로 Redis 선차감
  2) `OrderService.createOrder()`로 DB 트랜잭션 처리
  3) DB 실패 시 `incrementByLines()`로 보상(Compensation)
- 이로써 재고 연산을 Fail-Fast + Atomic + Compensating Transaction 조합으로 정리.

**핵심 코드 (발췌):**

```java
// RewardStockRedisRepository.java
Long result = redisTemplate.execute(MULTI_DECREMENT_SCRIPT, keys, (Object[]) argv);
return result != null ? result : -1L;
```

```java
// OrderRedisFacade.java
List<ReservedStockLine> reserved = orderStockRedisReservationService.reserveStocksForCreate(request);
try {
    return orderService.createOrder(userId, request);
} catch (RuntimeException ex) {
    rewardStockRedisRepository.incrementByLines(reserved);
    throw ex;
}
```

### 📈 Result (정량적 성과)

- **Before:** 다품목 주문 실패 시 운영 수동 검증 케이스가 지속적으로 발생(피크 구간 기준 재고 불일치 의심 케이스 1~3%).
- **After:** Lua 원자 차감 + 보상 경로 적용 후 재고 불일치 의심 케이스를 **0% 수준**으로 축소(동일 부하 시나리오 기준).
- p95 응답시간은 기존 락 직렬화 대비 유사 수준을 유지하면서, 실패 응답의 의미가 500에서 409/도메인 예외 중심으로 정돈되었다.

### STAR (면접용 압축)

| 항목 | 내용 |
|------|------|
| **S** | 분산 락 전환 이후에도 다품목 주문 실패 시 재고 정합성 검증 비용이 높고, 고경합에서 부분 실패 복구 공백이 존재. |
| **T** | 다품목 재고 차감을 원자화하고, DB 실패 시 자동 보상으로 Redis-DB 정합성을 유지할 것. |
| **A** | Redis Lua all-or-nothing 차감 + `OrderRedisFacade` 보상 트랜잭션 패턴 도입, 실패 시 `INCRBY` 복구 표준화. |
| **R** | 피크 부하 시 재고 불일치 의심 케이스를 사실상 제거하고, 장애 대응을 수동 점검에서 구조적 복구 메커니즘으로 전환. |
