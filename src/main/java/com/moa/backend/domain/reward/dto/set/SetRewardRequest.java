package com.moa.backend.domain.reward.dto.set;

import com.moa.backend.domain.reward.dto.select.OptionGroupRequest;
import com.moa.backend.domain.reward.entity.OptionGroup;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SetRewardRequest {

    private String setName;

    private Integer stockQuantity;

    private List<OptionGroupRequest> optionGroups;
}
