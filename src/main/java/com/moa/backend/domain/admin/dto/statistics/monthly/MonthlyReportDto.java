package com.moa.backend.domain.admin.dto.statistics.monthly;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * 월별 리포트 최상위 DTO
 */
@Getter
@Builder
@AllArgsConstructor
public class MonthlyReportDto {

    private String targetMonth;
    private String compareMonth;
    private MonthlyKpiDto kpi;
    private MonthlyTrendChartDto trendChart;
    private SuccessRateDto successRate;
    private GoalAmountRangeDto goalAmountRange;
    private CategorySuccessRateDto categorySuccessRate;
    private RetentionDto retention;
}
