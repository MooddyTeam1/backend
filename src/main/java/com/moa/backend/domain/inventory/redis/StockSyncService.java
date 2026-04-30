package com.moa.backend.domain.inventory.redis;

import com.moa.backend.domain.reward.entity.Reward;
import com.moa.backend.domain.reward.repository.RewardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * DB {@code rewards.stock_quantity} ↔ Redis {@code reward:stock:{id}} 동기화.
 * <p>
 * 기동 시 전량 적재는 대규모 테이블에서 부담될 수 있어 {@code app.stock.redis.sync-on-startup=false} 로 끌 수 있다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StockSyncService {

    private final RewardRepository rewardRepository;
    private final RewardStockRedisRepository rewardStockRedisRepository;

    @Value("${app.stock.redis.sync-on-startup:true}")
    private boolean syncOnStartup;

    @Order(100)
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        if (!syncOnStartup) {
            log.info("Redis 재고 기동 동기화 생략 (app.stock.redis.sync-on-startup=false)");
            return;
        }
        syncAllRewardsFromDatabase();
    }

    @Transactional(readOnly = true)
    public void syncAllRewardsFromDatabase() {
        int finite = 0;
        int deleted = 0;
        for (Reward r : rewardRepository.findAll()) {
            if (r.getStockQuantity() == null) {
                rewardStockRedisRepository.deleteStockKey(r.getId());
                deleted++;
            } else {
                rewardStockRedisRepository.overwriteStock(r.getId(), r.getStockQuantity());
                finite++;
            }
        }
        log.info("Redis 재고 동기화 완료: 유한 재고 {}건 키 SET, 무제한(null) {}건 키 DEL", finite, deleted);
    }

    @Transactional(readOnly = true)
    public void syncRewardFromDatabase(long rewardId) {
        rewardRepository.findById(rewardId).ifPresent(this::syncSingleRewardEntity);
    }

    public void syncSingleRewardEntity(Reward reward) {
        if (reward.getStockQuantity() == null) {
            rewardStockRedisRepository.deleteStockKey(reward.getId());
        } else {
            rewardStockRedisRepository.overwriteStock(reward.getId(), reward.getStockQuantity());
        }
    }
}
