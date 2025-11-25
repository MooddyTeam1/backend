package com.moa.backend.domain.reward.dto.select;

import com.moa.backend.domain.reward.entity.OptionValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "리워드 옵션 값 응답")
public class OptionValueResponse {
    @Schema(description = "옵션 값", example = "블랙")
    private String optionValue;

    @Schema(description = "추가 금액", example = "1000")
    private Long addPrice;

    @Schema(description = "옵션별 재고", example = "50")
    private Integer stockQuantity;

    public static OptionValueResponse from(OptionValue optionValue) {
        return OptionValueResponse.builder()
                .optionValue(optionValue.getOptionValue())
                .addPrice(optionValue.getAddPrice())
                .stockQuantity(optionValue.getStockQuantity())
                .build();
    }
}
