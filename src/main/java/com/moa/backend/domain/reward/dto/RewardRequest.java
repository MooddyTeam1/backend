package com.moa.backend.domain.reward.dto;

import com.moa.backend.domain.reward.dto.select.OptionGroupRequest;
import com.moa.backend.domain.reward.dto.set.SetRewardRequest;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RewardRequest {

    private String name;

    private String description;

    @Positive(message = "가격은 0보다 커야 합니다.")
    private Long price;

    @Positive(message = "수량은 0보다 커야 합니다.")
    private Integer stockQuantity;

    private LocalDate estimatedDeliveryDate;

    private boolean active=true;

    private List<OptionGroupRequest> optionGroups;
    private List<SetRewardRequest> setRewards;
}
