package com.moa.backend.domain.admin.controller;

import com.moa.backend.domain.admin.dto.statistics.dashboard.DashboardSummaryDto;
import com.moa.backend.domain.admin.dto.statistics.daily.DailyStatisticsDto;
import com.moa.backend.domain.admin.dto.statistics.revenue.RevenueReportDto;
import com.moa.backend.domain.admin.dto.statistics.monthly.MonthlyReportDto;
import com.moa.backend.domain.admin.dto.statistics.performance.ProjectPerformanceDto;
import com.moa.backend.domain.admin.service.StatisticsService;
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
public class StatisticsAdminController {

    private final StatisticsService statisticsService;

    @GetMapping("/dashboard")
    public DashboardSummaryDto getDashboardSummary() {
        return statisticsService.getDashboardSummary();
    }

    @GetMapping("/daily")
    public DailyStatisticsDto getDailyStatistics(
            @RequestParam("startDate") java.time.LocalDate startDate,
            @RequestParam("endDate") java.time.LocalDate endDate,
            @RequestParam(value = "filterType", required = false) String filterType,
            @RequestParam(value = "filterValue", required = false) String filterValue
    ) {
        return statisticsService.getDailyStatistics(startDate, endDate, filterType, filterValue);
    }

    @GetMapping("/revenue")
    public RevenueReportDto getRevenueReport(
            @RequestParam("startDate") java.time.LocalDate startDate,
            @RequestParam("endDate") java.time.LocalDate endDate,
            @RequestParam(value = "makerId", required = false) Long makerId,
            @RequestParam(value = "projectId", required = false) Long projectId
    ) {
        return statisticsService.getRevenueReport(startDate, endDate, makerId, projectId);
    }

    @GetMapping("/monthly")
    public MonthlyReportDto getMonthlyReport(
            @RequestParam("targetMonth") String targetMonth,
            @RequestParam(value = "compareMonth", required = false) String compareMonth
    ) {
        return statisticsService.getMonthlyReport(targetMonth, compareMonth);
    }

    @GetMapping("/project-performance")
    public ProjectPerformanceDto getProjectPerformance(
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "makerId", required = false) Long makerId
    ) {
        return statisticsService.getProjectPerformance(category, makerId);
    }
}
