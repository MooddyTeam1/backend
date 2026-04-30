package com.moa.backend.domain.inventory.redis;

import com.moa.backend.domain.order.dto.ReservedStockLine;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * 리워드 재고 Redis 문자열 값 + Lua 원자 차감/보상 INCR.
 */
@Repository
@RequiredArgsConstructor
public class RewardStockRedisRepository {

    /**
     * KEYS[i] 에 대해 ARGV[i] 만큼 차감. 전수 검사 후 한 번에 DECRBY.
     * 반환: 0 성공, -1 재고 부족 또는 키 없음, 수량 불량.
     */
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

    private final StringRedisTemplate redisTemplate;

    /** DB 기준 값으로 덮어쓰기 (동기화·관리자 수정). */
    public void overwriteStock(long rewardId, int quantity) {
        redisTemplate.opsForValue().set(RewardStockRedisKey.of(rewardId), String.valueOf(quantity));
    }

    /** 무제한 재고 등 — 키 제거. */
    public void deleteStockKey(long rewardId) {
        redisTemplate.delete(RewardStockRedisKey.of(rewardId));
    }

    /**
     * 키가 없을 때만 DB 스냅샷으로 생성({@code SETNX}).
     * 무제한(null) 재고 리워드는 호출하지 말 것.
     */
    public void ensureKeyFromDbSnapshot(long rewardId, int dbStockQuantity) {
        redisTemplate.opsForValue().setIfAbsent(
                RewardStockRedisKey.of(rewardId),
                String.valueOf(dbStockQuantity)
        );
    }

    /**
     * Lua 다중 차감. {@code lines} 비어 있으면 0 반환(노오퍼).
     */
    public long tryDecrementAllOrNothing(List<ReservedStockLine> lines) {
        if (lines.isEmpty()) {
            return 0L;
        }
        List<String> keys = new ArrayList<>(lines.size());
        String[] argv = new String[lines.size()];
        for (int i = 0; i < lines.size(); i++) {
            ReservedStockLine line = lines.get(i);
            keys.add(RewardStockRedisKey.of(line.rewardId()));
            argv[i] = String.valueOf(line.quantity());
        }
        Long result = redisTemplate.execute(MULTI_DECREMENT_SCRIPT, keys, (Object[]) argv);
        return result != null ? result : -1L;
    }

    /** 주문 실패 시 Redis 보상. */
    public void incrementByLines(List<ReservedStockLine> lines) {
        for (ReservedStockLine line : lines) {
            if (line.quantity() <= 0) {
                continue;
            }
            redisTemplate.opsForValue().increment(RewardStockRedisKey.of(line.rewardId()), line.quantity());
        }
    }

    /** 취소 시 단건 복구. 키가 없으면 생성하지 않고 스킵(무제한 재고 주문 등). */
    public void incrementIfPresent(long rewardId, int quantity) {
        if (quantity <= 0) {
            return;
        }
        String key = RewardStockRedisKey.of(rewardId);
        if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
            redisTemplate.opsForValue().increment(key, quantity);
        }
    }
}
