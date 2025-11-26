package com.moa.backend.domain.reward.service;

import com.moa.backend.domain.reward.dto.RewardStockIncreaseResponse;
import com.moa.backend.domain.reward.dto.RewardStockIncreaseRequest;

public interface RewardService {

    //리워드 추가
    RewardStockIncreaseResponse increaseStock(Long rewardId, RewardStockIncreaseRequest request, Long userId);
}
