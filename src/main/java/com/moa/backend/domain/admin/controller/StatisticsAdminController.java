package com.moa.backend.domain.admin.controller;

import com.moa.backend.domain.admin.dto.statistics.dashboard.DashboardSummaryDto;
import com.moa.backend.domain.admin.dto.statistics.daily.DailyStatisticsDto;
import com.moa.backend.domain.admin.dto.statistics.revenue.RevenueReportDto;
import com.moa.backend.domain.admin.dto.statistics.monthly.MonthlyReportDto;
import com.moa.backend.domain.admin.dto.statistics.performance.ProjectPerformanceDto;
import com.moa.backend.domain.admin.service.StatisticsService;
import com.moa.backend.global.error.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @Operation(summary = "대시보드 요약", description = "관리자 대시보드 KPI 요약 데이터를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "대시보드 조회 성공"),
            @ApiResponse(responseCode = "403", description = "관리자 권한 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public DashboardSummaryDto getDashboardSummary() {
        return statisticsService.getDashboardSummary();
    }

    @GetMapping("/daily")
    @Operation(summary = "일간 통계 조회", description = "startDate~endDate 구간 일간 통계를 조회합니다. 시작일이 종료일보다 늦으면 조회할 수 없습니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "일간 통계 조회 성공"),
            @ApiResponse(responseCode = "400", description = "날짜 범위 또는 필터 값이 유효하지 않음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "관리자 권한 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public DailyStatisticsDto getDailyStatistics(
            @Parameter(example = "2025-11-01") @RequestParam("startDate") java.time.LocalDate startDate,
            @Parameter(example = "2025-11-30") @RequestParam("endDate") java.time.LocalDate endDate,
            @Parameter(description = "필터 타입(e.g. CATEGORY/MAKER/PROJECT)", example = "PROJECT") @RequestParam(value = "filterType", required = false) String filterType,
            @Parameter(description = "필터 값", example = "1201") @RequestParam(value = "filterValue", required = false) String filterValue
    ) {
        return statisticsService.getDailyStatistics(startDate, endDate, filterType, filterValue);
    }

    @GetMapping("/revenue")
    @Operation(summary = "매출/정산 리포트 조회", description = "기간별 매출/정산 리포트를 조회합니다. makerId/projectId는 선택 필터입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "매출 리포트 조회 성공"),
            @ApiResponse(responseCode = "400", description = "요청 파라미터가 유효하지 않음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "관리자 권한 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public RevenueReportDto getRevenueReport(
            @Parameter(example = "2025-11-01") @RequestParam("startDate") java.time.LocalDate startDate,
            @Parameter(example = "2025-11-30") @RequestParam("endDate") java.time.LocalDate endDate,
            @Parameter(example = "1003") @RequestParam(value = "makerId", required = false) Long makerId,
            @Parameter(example = "1201") @RequestParam(value = "projectId", required = false) Long projectId
    ) {
        return statisticsService.getRevenueReport(startDate, endDate, makerId, projectId);
    }

    @GetMapping("/monthly")
    @Operation(summary = "월간 리포트 조회", description = "targetMonth 기준 월간 리포트를 조회하고, compareMonth 지정 시 증감 비교를 제공합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "월간 리포트 조회 성공"),
            @ApiResponse(responseCode = "400", description = "월 형식(yyyy-MM) 오류", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "관리자 권한 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public MonthlyReportDto getMonthlyReport(
            @Parameter(example = "2025-11") @RequestParam("targetMonth") String targetMonth,
            @Parameter(example = "2025-10") @RequestParam(value = "compareMonth", required = false) String compareMonth
    ) {
        return statisticsService.getMonthlyReport(targetMonth, compareMonth);
    }

    @GetMapping("/project-performance")
    @Operation(summary = "프로젝트 퍼포먼스 조회", description = "카테고리/메이커 조건으로 프로젝트 성과 분석 데이터를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "퍼포먼스 조회 성공"),
            @ApiResponse(responseCode = "400", description = "필터 값이 유효하지 않음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "관리자 권한 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ProjectPerformanceDto getProjectPerformance(
            @Parameter(example = "TECH") @RequestParam(value = "category", required = false) String category,
            @Parameter(example = "1003") @RequestParam(value = "makerId", required = false) Long makerId
    ) {
        return statisticsService.getProjectPerformance(category, makerId);
    }

    @GetMapping("/funnel")
    @Operation(summary = "퍼널 리포트 조회", description = "지정 기간의 방문-전환 퍼널 리포트를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "퍼널 리포트 조회 성공"),
            @ApiResponse(responseCode = "400", description = "요청 파라미터가 유효하지 않음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "관리자 권한 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public com.moa.backend.domain.admin.dto.statistics.funnel.FunnelReportDto getFunnelReport(
            @Parameter(example = "2025-11-01") @RequestParam("startDate") java.time.LocalDate startDate,
            @Parameter(example = "2025-11-30") @RequestParam("endDate") java.time.LocalDate endDate,
            @Parameter(example = "1201") @RequestParam(value = "projectId", required = false) Long projectId
    ) {
        return statisticsService.getFunnelReport(startDate, endDate, projectId);
    }
}
