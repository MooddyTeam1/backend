package com.moa.backend.domain.reward.service;

import com.moa.backend.domain.maker.entity.Maker;
import com.moa.backend.domain.reward.dto.RewardStockIncreaseResponse;
import com.moa.backend.domain.reward.dto.RewardStockIncreaseRequest;
import com.moa.backend.domain.reward.entity.Reward;
import com.moa.backend.domain.reward.repository.RewardRepository;
import com.moa.backend.global.error.AppException;
import com.moa.backend.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class RewardServiceImpl implements RewardService {

    private final RewardRepository rewardRepository;

    @Override
    @Transactional
    public RewardStockIncreaseResponse increaseStock(Long rewardId, RewardStockIncreaseRequest request, Long userId) {
        Reward reward = rewardRepository.findById(rewardId)
                .orElseThrow(() -> new AppException(ErrorCode.REWARD_NOT_FOUND));

        Maker maker = reward.getProject().getMaker();
        if (maker == null || maker.getOwner() == null) {
            throw new AppException(ErrorCode.FORBIDDEN, "메이커 정보가 없어 재고 수정이 불가능합니다.");
        }

        reward.increaseStock(request.getQuantity());

        return new RewardStockIncreaseResponse(reward.getId(), reward.getStockQuantity());
    }
}
