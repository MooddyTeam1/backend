package com.moa.backend.domain.reward.dto.select;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "리워드 옵션 값 요청")
public class OptionValueRequest {

    @Schema(description = "옵션 값", example = "블랙")
    private String optionValue;

    @Schema(description = "추가 금액", example = "1000")
    private Long addPrice;

    @Schema(description = "옵션별 재고 수량", example = "50")
    private Integer stockQuantity;
}
