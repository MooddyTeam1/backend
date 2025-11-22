package com.moa.backend.domain.admin.dto.statistics.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * 대시보드 요약 DTO (최상위)
 * API: GET /api/admin/statistics/dashboard
 */
@Getter
@Builder
@AllArgsConstructor
public class DashboardSummaryDto {

    private KpiSummaryDto kpiSummary;                    // KPI 6개 요약
    private TrendChartDto trendChart;                    // 트렌드 차트
    private CategoryPerformanceDto categoryPerformance;  // 카테고리별 성과 Top 4
    private List<TopProjectDto> topProjects;             // 상위 프로젝트 Top 5
    private List<AlertDto> alerts;                       // 알림 목록
}
