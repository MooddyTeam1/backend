package com.moa.backend.domain.admin.dto.statistics.revenue;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * 수익 리포트 최상위 DTO
 */
@Getter
@Builder
@AllArgsConstructor
public class RevenueReportDto {

    private PlatformRevenueDto platformRevenue;
    private MakerSettlementSummaryDto makerSettlementSummary;
    private FeePolicyAnalysisDto feePolicyAnalysis;
    private List<RevenueDetailDto> details;
}
