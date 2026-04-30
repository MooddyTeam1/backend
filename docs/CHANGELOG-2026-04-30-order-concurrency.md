# 변경 이력 리포트 (2026-04-30)

## 1) 커밋 전 상태 (AS-IS)

### 브랜치/원격
- Branch: `develop`
- Upstream: `origin/develop`
- 저장소: `https://github.com/MooddyTeam1/backend.git`

### 변경 파일 규모 (커밋 직전)
- 총 변경 파일: 50+ (스테이징 + 워킹트리 혼합)
- 핵심 코드 변화(working tree stat): `41 files changed, 677 insertions(+), 426 deletions(-)`
- 신규 대규모 자산:
  - `k6.json` (k6 시나리오/리포트)
  - Redis 재고 선차감/보상 관련 도메인 클래스
  - Grafana/Prometheus 프로비저닝 파일

### 커밋 전 문제/배경
- 기존 동시성 제어(@Version + 재시도/스핀 대기)는 주문 집중 구간에서 DB 경합과 실패 응답 품질(500 노출 가능성)에 취약했다.
- 분산락 기반 직렬화(Phase 2) 이후에도 다품목 주문의 원자적 선차감/보상에 대한 요구가 남아 있었다.
- 테스트/운영 관측 관점에서 k6 토큰 발급, 테스트 데이터 초기화, 예외 진단 정보 강화가 필요했다.

---

## 2) 이번 변경으로 추가/개선된 기능 (TO-BE)

### A. 주문 동시성 제어 고도화 (Redisson 락 -> Redis Lua 원자 선차감)
- `OrderRedisFacade` 도입:
  - 주문 생성 전 Redis 재고 선차감 수행
  - DB 저장 실패 시 Redis 재고 보상(`INCRBY`) 수행
- `OrderStockRedisReservationService` 도입:
  - 프로젝트/리워드/수량 유효성 검증
  - 유한 재고만 분리하여 Lua 스크립트로 all-or-nothing 차감
- `RewardStockRedisRepository` 도입:
  - 다중 키 원자 차감 Lua (`tryDecrementAllOrNothing`)
  - 실패 보상/취소 복구용 증분 API 제공
- `OrderController`가 `OrderRedisFacade`를 호출하도록 전환되어, 동시 주문에서 재고 정합성 보장 범위를 확장

### B. 재고 동기화/취소 정합성 보강
- `StockSyncService`, `RewardServiceImpl` 변경으로 DB 재고와 Redis 키 동기화 경로 보강
- 주문 취소 시 DB 재고 복구와 함께 Redis 키가 존재하는 경우 `incrementIfPresent`로 보상

### C. 관측성/예외 대응 강화
- `UnhandledExceptionDiagnostics` 신규 도입
- `GlobalExceptionHandler` 강화:
  - 미처리 예외 root cause 분류
  - dev 프로필에서 진단 payload 포함
- `OrderCreationMetrics` 개선:
  - Redis 보상 실패/락 획득 실패 등 운영 지표 추가

### D. 성능/부하테스트/운영 편의 기능 추가
- `k6/k6-load-test.js`, `k6.json`, `k6/RUNBOOK.txt` 확장
- 토큰/시드/리셋 관련 초기화 컨트롤러·서비스 추가:
  - `K6TokenExportController`, `K6TestResetController`, `UserSeedingService`, `TestDataInitController`
- 모니터링 배포 자산 추가:
  - `grafana/provisioning/...`
  - `prometheus.yml`

### E. 보안/설정/리포지토리 보강
- `SecurityConfig`, `application.yml`, `application-dev.yml`, `application-test.yml` 조정
- 리포지토리 계층 쿼리/조회 보강:
  - `OrderRepository`, `PaymentRepository`, `UserRepository`, `ProjectWalletTransactionRepository`

---

## 3) 핵심 코드 발췌 (왜 이렇게 바꿨는가)

### Redis Lua 원자 차감
```java
private static final DefaultRedisScript<Long> MULTI_DECREMENT_SCRIPT = new DefaultRedisScript<>(
        """
                local n = #KEYS
                for i = 1, n do
                  local need = tonumber(ARGV[i])
                  if need == nil or need < 1 then
                    return -1
                  end
                  local raw = redis.call('GET', KEYS[i])
                  if raw == false then
                    return -1
                  end
                  local cur = tonumber(raw)
                  if cur == nil or cur < need then
                    return -1
                  end
                end
                for i = 1, n do
                  redis.call('DECRBY', KEYS[i], tonumber(ARGV[i]))
                end
                return 0
                """,
        Long.class
);
```

### DB 실패 시 보상 트랜잭션 외곽 처리
```java
public OrderDetailResponse createOrder(Long userId, OrderCreateRequest request) {
    List<ReservedStockLine> reserved = orderStockRedisReservationService.reserveStocksForCreate(request);
    boolean needCompensate = !reserved.isEmpty();
    try {
        return orderService.createOrder(userId, request);
    } catch (RuntimeException ex) {
        if (needCompensate) {
            rewardStockRedisRepository.incrementByLines(reserved);
        }
        throw ex;
    }
}
```

---

## 4) 커밋 후 상태 (결과 기록)

> 이 섹션은 커밋/푸시 완료 후 실제 SHA와 함께 확정 기록한다.

- Commit SHA: `b986ca1`
- Commit message: `feat(order): Redis Lua 재고 선차감 기반 주문 동시성 처리 고도화`
- Push result: `origin/develop` 반영 완료 (`6ac284a -> b986ca1`)
- 작업트리 상태: clean

---

## 5) 기대 효과 (운영 관점)
- 주문 폭주 구간에서 다품목 재고 차감의 원자성 보장 강화
- DB 트랜잭션 실패 시 Redis 재고 유실 위험 완화(보상 경로 추가)
- 5xx 비정상 실패를 비즈니스 충돌(409) 중심으로 정돈할 수 있는 토대 확보
- k6 + Grafana/Prometheus 기반으로 병목과 실패 유형의 관측/재현 가능성 향상
