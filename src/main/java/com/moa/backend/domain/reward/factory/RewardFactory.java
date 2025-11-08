package com.moa.backend.domain.reward.factory;

import com.moa.backend.domain.project.entity.Project;
import com.moa.backend.domain.reward.dto.RewardRequest;
import com.moa.backend.domain.reward.dto.select.OptionGroupRequest;
import com.moa.backend.domain.reward.dto.set.SetRewardRequest;
import com.moa.backend.domain.reward.entity.OptionGroup;
import com.moa.backend.domain.reward.entity.OptionValue;
import com.moa.backend.domain.reward.entity.Reward;
import com.moa.backend.domain.reward.entity.RewardSet;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@Component
public class RewardFactory {

    public Reward createReward(Project project, RewardRequest r) {
        Reward reward = Reward.builder()
                .project(project)
                .name(r.getName())
                .description(r.getDescription())
                .stockQuantity(r.getStockQuantity())
                .price(r.getPrice())
                .estimatedDeliveryDate(r.getEstimatedDeliveryDate())
                .active(r.isActive())
                .build();

        // 옵션 그룹 처리
        if (!CollectionUtils.isEmpty(r.getOptionGroups())) {
            r.getOptionGroups().forEach(g -> reward.addOptionGroup(toOptionGroup(g)));
        }

        // 세트 리워드 처리
        if (!CollectionUtils.isEmpty(r.getSetRewards())) {
            r.getSetRewards().forEach(s -> reward.addRewardSet(toRewardSet(s)));
        }

        return reward;
    }

    private OptionGroup toOptionGroup(OptionGroupRequest g) {
        OptionGroup group = OptionGroup.builder()
                .groupName(g.getGroupName())
                .build();

        if (!CollectionUtils.isEmpty(g.getOptionValues())) {
            g.getOptionValues().forEach(v -> {
                OptionValue value = OptionValue.builder()
                        .optionValue(v.getOptionValue())
                        .addPrice(v.getAddPrice())
                        .stockQuantity(v.getStockQuantity())
                        .build();
                group.addOptionValue(value);
            });
        }
        return group;
    }

    private RewardSet toRewardSet(SetRewardRequest s) {
        RewardSet rewardSet = RewardSet.builder()
                .setName(s.getSetName())
                .stockQuantity(s.getStockQuantity())
                .build();

        if (!CollectionUtils.isEmpty(s.getOptionGroups())) {
            s.getOptionGroups().forEach(g -> rewardSet.addOptionGroup(toOptionGroup(g)));
        }
        return rewardSet;
    }
}