package com.moa.backend.domain.inventory.redis;

/**
 * Redis 재고 키 규약: {@code reward:stock:{rewardId}}.
 * <p>
 * Redis Cluster 전환 시 동일 슬롯 강제가 필요하면 {@code {reward:123}:stock} 형태의 해시 태그로 변경한다.
 */
public final class RewardStockRedisKey {

    private static final String PREFIX = "reward:stock:";

    private RewardStockRedisKey() {
    }

    public static String of(long rewardId) {
        return PREFIX + rewardId;
    }
}
