package com.moa.backend.domain.admin.dto.statistics.monthly;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * 목표금액 구간별 성공률
 */
@Getter
@Builder
@AllArgsConstructor
@Schema(description = "목표금액 구간별 성공률")
public class GoalAmountRangeDto {

    @Schema(description = "구간 목록")
    private List<GoalRangeItemDto> ranges;
}
