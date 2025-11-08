package com.moa.backend.domain.reward.dto;

import com.moa.backend.domain.reward.entity.Reward;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RewardResponse {

    private String name;
    private String description;
    private Integer stockQuantity;
    private Long price;
    private LocalDate estimatedDeliveryDate;
    private boolean active = true;

    public static RewardResponse from(Reward reward) {
        return RewardResponse.builder()
                .name(reward.getName())
                .description(reward.getDescription())
                .stockQuantity(reward.getStockQuantity())
                .price(reward.getPrice())
                .estimatedDeliveryDate(reward.getEstimatedDeliveryDate())
                .active(reward.isActive())
                .build();
    }
}
