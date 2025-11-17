package com.moa.backend.domain.reward.dto.select;

import com.moa.backend.domain.reward.entity.OptionValue;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OptionValueResponse {
    private String optionValue;

    private Long addPrice;

    private Integer stockQuantity;

    public static OptionValueResponse from(OptionValue optionValue) {
        return OptionValueResponse.builder()
                .optionValue(optionValue.getOptionValue())
                .addPrice(optionValue.getAddPrice())
                .stockQuantity(optionValue.getStockQuantity())
                .build();
    }
}
