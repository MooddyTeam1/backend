package com.moa.backend.domain.admin.service;

import com.moa.backend.domain.admin.dto.statistics.dashboard.DashboardSummaryDto;
import com.moa.backend.domain.admin.dto.statistics.daily.DailyStatisticsDto;
import com.moa.backend.domain.admin.dto.statistics.revenue.RevenueReportDto;

public interface StatisticsService {

    /**
     * 요약 대시보드 통계 조회
     */
    DashboardSummaryDto getDashboardSummary();

    /**
     * 일일 통계 조회
     */
    DailyStatisticsDto getDailyStatistics(
            java.time.LocalDate startDate,
            java.time.LocalDate endDate,
            String filterType,
            String filterValue
    );

    /**
     * 수익 리포트 조회
     */
    RevenueReportDto getRevenueReport(
            java.time.LocalDate startDate,
            java.time.LocalDate endDate,
            Long makerId,
            Long projectId
    );
}
