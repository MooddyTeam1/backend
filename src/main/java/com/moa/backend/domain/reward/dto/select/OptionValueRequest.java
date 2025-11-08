package com.moa.backend.domain.reward.dto.select;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OptionValueRequest {

    private String optionValue;

    private Long addPrice;

    private Integer stockQuantity;
}
