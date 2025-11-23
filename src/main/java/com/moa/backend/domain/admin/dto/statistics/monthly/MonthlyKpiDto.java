package com.moa.backend.domain.admin.dto.statistics.monthly;

import com.moa.backend.domain.admin.dto.statistics.dashboard.KpiItemDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * 월별 KPI 묶음
 */
@Getter
@Builder
@AllArgsConstructor
public class MonthlyKpiDto {

    private KpiItemDto totalFundingAmount;
    private Integer successProjectCount;
    private Integer failedProjectCount;
    private Integer newMakerCount;
    private Integer newSupporterCount;
}
