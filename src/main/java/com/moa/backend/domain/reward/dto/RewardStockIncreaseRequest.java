package com.moa.backend.domain.reward.dto;

import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RewardStockIncreaseRequest {

    @Positive(message = "추가 수량은 1 이상이어야 합니다.")
    private Integer quantity;
}
