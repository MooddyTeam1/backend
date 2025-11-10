package com.moa.backend.domain.reward.dto.set;

import com.moa.backend.domain.reward.dto.select.OptionGroupResponse;
import com.moa.backend.domain.reward.entity.RewardSet;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RewardSetResponse {
    private String setName;

    private Integer stockQuantity;

    private List<OptionGroupResponse> optionGroups;

    public static RewardSetResponse from(RewardSet rewardSet) {
        return RewardSetResponse.builder()
                .setName(rewardSet.getSetName())
                .stockQuantity(rewardSet.getStockQuantity())
                .optionGroups(rewardSet.getOptionGroups() != null ?
                        rewardSet.getOptionGroups().stream().map(OptionGroupResponse::from).toList() : null)
                .build();
    }
}
