package com.moa.backend.domain.reward.dto.select;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OptionGroupRequest {
    private String groupName;

    private List<OptionValueRequest> optionValues;
}
