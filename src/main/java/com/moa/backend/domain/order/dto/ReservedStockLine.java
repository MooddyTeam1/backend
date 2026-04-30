package com.moa.backend.domain.order.dto;

/**
 * Redis Lua 차감·보상 INCR 시 사용하는 (리워드 ID, 수량) 쌍.
 */
public record ReservedStockLine(long rewardId, int quantity) {
}
