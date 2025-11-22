package com.moa.backend.domain.admin.dto.statistics.monthly;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * 목표금액 구간 단위
 */
@Getter
@Builder
@AllArgsConstructor
public class GoalRangeItemDto {

    private String rangeName;
    private Integer successCount;
    private Integer totalCount;
    private Double successRate;
}
