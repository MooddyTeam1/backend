package com.moa.backend.domain.reward.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RewardStockIncreaseResponse {
    private Long rewardId;
    private Integer stockQuantity;
}