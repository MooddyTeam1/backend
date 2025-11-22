package com.moa.backend.domain.admin.dto.statistics.monthly;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * 목표금액 구간별 성공률
 */
@Getter
@Builder
@AllArgsConstructor
public class GoalAmountRangeDto {

    private List<GoalRangeItemDto> ranges;
}
