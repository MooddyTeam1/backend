package com.moa.backend.domain.reward.dto;

import com.moa.backend.domain.reward.dto.select.OptionGroupResponse;
import com.moa.backend.domain.reward.dto.set.RewardSetResponse;
import com.moa.backend.domain.reward.entity.Reward;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RewardResponse {

    private String name;
    private String description;
    private Integer stockQuantity;
    private Long price;
    private LocalDate estimatedDeliveryDate;
    private boolean active = true;

    private List<OptionGroupResponse> optionGroups;
    private List<RewardSetResponse> rewardSets;

    public static RewardResponse from(Reward reward) {
        return RewardResponse.builder()
                .name(reward.getName())
                .description(reward.getDescription())
                .stockQuantity(reward.getStockQuantity())
                .price(reward.getPrice())
                .estimatedDeliveryDate(reward.getEstimatedDeliveryDate())
                .active(reward.isActive())
                .rewardSets(reward.getRewardSets() != null ? reward.getRewardSets().stream()
                        .map(RewardSetResponse::from).toList() : null)
                .optionGroups(reward.getOptionGroups() != null ? reward.getOptionGroups().stream()
                        .map(OptionGroupResponse::from).toList() : null)
                .build();
    }
}
