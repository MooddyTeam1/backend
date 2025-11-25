package com.moa.backend.domain.reward.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "리워드 재고 추가 요청")
public class RewardStockIncreaseRequest {

    @Schema(description = "추가 수량(1 이상)", example = "10")
    @Positive(message = "추가 수량은 1 이상이어야 합니다.")
    private Integer quantity;
}
