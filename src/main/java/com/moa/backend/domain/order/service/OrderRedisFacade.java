package com.moa.backend.domain.order.service;

import com.moa.backend.domain.inventory.redis.RewardStockRedisRepository;
import com.moa.backend.domain.order.dto.OrderCreateRequest;
import com.moa.backend.domain.order.dto.OrderDetailResponse;
import com.moa.backend.domain.order.dto.OrderPageResponse;
import com.moa.backend.domain.order.dto.ReservedStockLine;
import com.moa.backend.domain.order.metrics.OrderCreationMetrics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Phase 3: Redis Lua 선차감 후 {@link OrderService#createOrder} 호출.
 * DB 저장 실패 시 Redis {@code INCRBY} 보상.
 * <p>
 * {@code @Transactional} 없음 — 트랜잭션 경계는 {@link OrderService} 에 둔다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderRedisFacade {

    private final OrderStockRedisReservationService orderStockRedisReservationService;
    private final OrderService orderService;
    private final RewardStockRedisRepository rewardStockRedisRepository;
    private final OrderCreationMetrics orderCreationMetrics;

    public OrderDetailResponse createOrder(Long userId, OrderCreateRequest request) {
        List<ReservedStockLine> reserved = orderStockRedisReservationService.reserveStocksForCreate(request);
        boolean needCompensate = !reserved.isEmpty();
        try {
            return orderService.createOrder(userId, request);
        } catch (RuntimeException ex) {
            if (needCompensate) {
                compensateRedisQuietly(reserved, ex);
            }
            throw ex;
        }
    }

    private void compensateRedisQuietly(List<ReservedStockLine> reserved, RuntimeException cause) {
        try {
            rewardStockRedisRepository.incrementByLines(reserved);
            log.warn("주문 DB 저장 실패 후 Redis 재고 보상 완료: lines={}, causeType={}",
                    reserved, cause.getClass().getSimpleName());
        } catch (Exception compensateEx) {
            log.error("주문 DB 저장 실패 후 Redis 재고 보상 실패 — 수동 정합성 점검 필요. reserved={}, original={}",
                    reserved, cause.toString(), compensateEx);
            orderCreationMetrics.recordRedisCompensationFailed();
        }
    }

    public void cancelOrder(Long userId, Long orderId, String reason) {
        orderService.cancelOrder(userId, orderId, reason);
    }

    public OrderDetailResponse getOrder(Long userId, Long orderId) {
        return orderService.getOrder(userId, orderId);
    }

    public OrderPageResponse getOrders(Long userId, int page, int size) {
        return orderService.getOrders(userId, page, size);
    }
}
