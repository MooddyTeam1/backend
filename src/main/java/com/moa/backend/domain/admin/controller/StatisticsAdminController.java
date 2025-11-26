package com.moa.backend.domain.admin.controller;

import com.moa.backend.domain.admin.dto.statistics.dashboard.DashboardSummaryDto;
import com.moa.backend.domain.admin.dto.statistics.daily.DailyStatisticsDto;
import com.moa.backend.domain.admin.dto.statistics.revenue.RevenueReportDto;
import com.moa.backend.domain.admin.dto.statistics.monthly.MonthlyReportDto;
import com.moa.backend.domain.admin.dto.statistics.performance.ProjectPerformanceDto;
import com.moa.backend.domain.admin.service.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/statistics")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Statistics-Admin", description = "관리자 통계/대시보드")
public class StatisticsAdminController {

    private final StatisticsService statisticsService;

    @GetMapping("/dashboard")
    @Operation(summary = "대시보드 요약")
    public DashboardSummaryDto getDashboardSummary() {
        return statisticsService.getDashboardSummary();
    }

    @GetMapping("/daily")
    @Operation(summary = "일간 통계 조회")
    public DailyStatisticsDto getDailyStatistics(
            @Parameter(example = "2025-11-01") @RequestParam("startDate") java.time.LocalDate startDate,
            @Parameter(example = "2025-11-30") @RequestParam("endDate") java.time.LocalDate endDate,
            @Parameter(description = "필터 타입(e.g. CATEGORY/MAKER/PROJECT)", example = "PROJECT") @RequestParam(value = "filterType", required = false) String filterType,
            @Parameter(description = "필터 값", example = "1201") @RequestParam(value = "filterValue", required = false) String filterValue
    ) {
        return statisticsService.getDailyStatistics(startDate, endDate, filterType, filterValue);
    }

    @GetMapping("/revenue")
    @Operation(summary = "매출/정산 리포트 조회")
    public RevenueReportDto getRevenueReport(
            @Parameter(example = "2025-11-01") @RequestParam("startDate") java.time.LocalDate startDate,
            @Parameter(example = "2025-11-30") @RequestParam("endDate") java.time.LocalDate endDate,
            @Parameter(example = "1003") @RequestParam(value = "makerId", required = false) Long makerId,
            @Parameter(example = "1201") @RequestParam(value = "projectId", required = false) Long projectId
    ) {
        return statisticsService.getRevenueReport(startDate, endDate, makerId, projectId);
    }

    @GetMapping("/monthly")
    @Operation(summary = "월간 리포트 조회")
    public MonthlyReportDto getMonthlyReport(
            @Parameter(example = "2025-11") @RequestParam("targetMonth") String targetMonth,
            @Parameter(example = "2025-10") @RequestParam(value = "compareMonth", required = false) String compareMonth
    ) {
        return statisticsService.getMonthlyReport(targetMonth, compareMonth);
    }

    @GetMapping("/project-performance")
    @Operation(summary = "프로젝트 퍼포먼스 조회")
    public ProjectPerformanceDto getProjectPerformance(
            @Parameter(example = "TECH") @RequestParam(value = "category", required = false) String category,
            @Parameter(example = "1003") @RequestParam(value = "makerId", required = false) Long makerId
    ) {
        return statisticsService.getProjectPerformance(category, makerId);
    }

    @GetMapping("/funnel")
    @Operation(summary = "퍼널 리포트 조회")
    public com.moa.backend.domain.admin.dto.statistics.funnel.FunnelReportDto getFunnelReport(
            @Parameter(example = "2025-11-01") @RequestParam("startDate") java.time.LocalDate startDate,
            @Parameter(example = "2025-11-30") @RequestParam("endDate") java.time.LocalDate endDate,
            @Parameter(example = "1201") @RequestParam(value = "projectId", required = false) Long projectId
    ) {
        return statisticsService.getFunnelReport(startDate, endDate, projectId);
    }
}
