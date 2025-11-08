package com.moa.backend.domain.reward.dto.set;

import com.moa.backend.domain.reward.dto.select.OptionGroupRequest;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RewardSetRequest {

    private String setName;

    private Integer stockQuantity;

    private List<OptionGroupRequest> optionGroups;
}
