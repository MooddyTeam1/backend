package com.moa.backend.domain.reward.service;

import com.moa.backend.domain.reward.dto.RewardResponse;
import com.moa.backend.domain.reward.dto.RewardStockIncreaseRequest;
import com.moa.backend.domain.reward.dto.RewardStockIncreaseResponse;

import java.util.List;

public interface RewardService {

    // 프로토타입 메이커용 재고 증가
    RewardStockIncreaseResponse increaseStock(Long rewardId, RewardStockIncreaseRequest request, Long userId);

    // 공개 프로젝트 상세 리워드 조회
    List<RewardResponse> getRewardsWithDisclosureByProjectId(Long projectId);
}
