package com.moa.backend.domain.order.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

/**
 * 주문 생성 결과를 Micrometer 로 집계한다.
 * <p>
 * Phase 3: DB 실패 후 Redis 보상(INCRBY) 도 실패한 횟수를 {@link #REDIS_COMPENSATION_FAILED} 로 집계한다.
 * <p>
 * 조회: {@code GET /actuator/metrics/orders.create.success} 등
 */
@Component
public class OrderCreationMetrics {

    /** 주문 생성 성공 */
    public static final String SUCCESS = "orders.create.success";

    /**
     * Lua 선차감 후 DB 저장 실패 → Redis 보상(INCRBY) 도 실패한 경우(수동 정합성 필요).
     */
    public static final String REDIS_COMPENSATION_FAILED = "orders.create.redis_compensation_failed";

    private final MeterRegistry registry;

    public OrderCreationMetrics(MeterRegistry registry) {
        this.registry = registry;
    }

    public void recordSuccess() {
        registry.counter(SUCCESS).increment();
    }

    public void recordRedisCompensationFailed() {
        registry.counter(REDIS_COMPENSATION_FAILED).increment();
    }
}
