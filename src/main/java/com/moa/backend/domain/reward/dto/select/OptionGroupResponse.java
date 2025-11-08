package com.moa.backend.domain.reward.dto.select;

import com.moa.backend.domain.reward.entity.OptionGroup;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OptionGroupResponse {
    private String groupName;

    private List<OptionValueResponse> optionValues;

    public static OptionGroupResponse from(OptionGroup optionGroup) {
        return OptionGroupResponse.builder()
                .groupName(optionGroup.getGroupName())
                .optionValues(optionGroup.getOptionValues() != null ?
                        optionGroup.getOptionValues().stream()
                                .map(OptionValueResponse::from).toList() : null)
                .build();
    }
}

