package com.moa.backend.domain.reward.dto.set;

import com.moa.backend.domain.reward.dto.select.OptionGroupRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "리워드 세트 요청")
public class RewardSetRequest {

    @Schema(description = "세트 이름", example = "텀블러+파우치 세트")
    private String setName;

    @Schema(description = "세트 재고 수량", example = "50")
    private Integer stockQuantity;

    @Schema(description = "옵션 그룹 목록")
    private List<OptionGroupRequest> optionGroups;
}
