package com.moa.backend.domain.admin.service;

import com.moa.backend.domain.admin.dto.statistics.dashboard.AlertDto;
import com.moa.backend.domain.admin.dto.statistics.dashboard.CategoryItemDto;
import com.moa.backend.domain.admin.dto.statistics.dashboard.CategoryPerformanceDto;
import com.moa.backend.domain.admin.dto.statistics.dashboard.DashboardSummaryDto;
import com.moa.backend.domain.admin.dto.statistics.dashboard.KpiItemDto;
import com.moa.backend.domain.admin.dto.statistics.dashboard.KpiSummaryDto;
import com.moa.backend.domain.admin.dto.statistics.dashboard.TopProjectDto;
import com.moa.backend.domain.admin.dto.statistics.dashboard.TrendChartDto;
import com.moa.backend.domain.admin.dto.statistics.dashboard.TrendDataDto;
import com.moa.backend.domain.admin.dto.statistics.daily.DailyStatisticsDto;
import com.moa.backend.domain.admin.dto.statistics.daily.HourlyChartDto;
import com.moa.backend.domain.admin.dto.statistics.daily.HourlyDataDto;
import com.moa.backend.domain.admin.dto.statistics.daily.MakerDetailDto;
import com.moa.backend.domain.admin.dto.statistics.daily.PaymentStatisticsDto;
import com.moa.backend.domain.admin.dto.statistics.daily.ProjectActivityDto;
import com.moa.backend.domain.admin.dto.statistics.daily.ProjectDetailDto;
import com.moa.backend.domain.admin.dto.statistics.daily.TrafficDto;
import com.moa.backend.domain.admin.dto.statistics.monthly.CategorySuccessItemDto;
import com.moa.backend.domain.admin.dto.statistics.monthly.CategorySuccessRateDto;
import com.moa.backend.domain.admin.dto.statistics.monthly.GoalAmountRangeDto;
import com.moa.backend.domain.admin.dto.statistics.monthly.GoalRangeItemDto;
import com.moa.backend.domain.admin.dto.statistics.monthly.MonthlyKpiDto;
import com.moa.backend.domain.admin.dto.statistics.monthly.MonthlyReportDto;
import com.moa.backend.domain.admin.dto.statistics.monthly.MonthlyTrendChartDto;
import com.moa.backend.domain.admin.dto.statistics.monthly.MonthlyTrendDataDto;
import com.moa.backend.domain.admin.dto.statistics.monthly.RetentionDto;
import com.moa.backend.domain.admin.dto.statistics.monthly.SuccessRateDto;
import com.moa.backend.domain.admin.dto.statistics.monthly.SuccessRateItemDto;
import com.moa.backend.domain.admin.dto.statistics.performance.CategoryAverageDto;
import com.moa.backend.domain.admin.dto.statistics.performance.MakerAverageDto;
import com.moa.backend.domain.admin.dto.statistics.performance.OpportunityProjectDto;
import com.moa.backend.domain.admin.dto.statistics.performance.ProjectPerformanceDto;
import com.moa.backend.domain.admin.dto.statistics.performance.ProjectPerformanceItemDto;
import com.moa.backend.domain.admin.dto.statistics.performance.RiskProjectDto;
import com.moa.backend.domain.admin.dto.statistics.revenue.FeePolicyAnalysisDto;
import com.moa.backend.domain.admin.dto.statistics.revenue.FeePolicyItemDto;
import com.moa.backend.domain.admin.dto.statistics.revenue.MakerSettlementSummaryDto;
import com.moa.backend.domain.admin.dto.statistics.revenue.PlatformRevenueDto;
import com.moa.backend.domain.admin.dto.statistics.revenue.RevenueDetailDto;
import com.moa.backend.domain.admin.dto.statistics.revenue.RevenueReportDto;
import com.moa.backend.domain.order.entity.OrderStatus;
import com.moa.backend.domain.order.repository.OrderRepository;
import com.moa.backend.domain.maker.repository.MakerRepository;
import com.moa.backend.domain.payment.entity.PaymentStatus;
import com.moa.backend.domain.payment.entity.RefundStatus;
import com.moa.backend.domain.payment.repository.PaymentRepository;
import com.moa.backend.domain.payment.repository.RefundRepository;
import com.moa.backend.domain.project.entity.Category;
import com.moa.backend.domain.project.entity.ProjectLifecycleStatus;
import com.moa.backend.domain.project.entity.ProjectReviewStatus;
import com.moa.backend.domain.project.entity.ProjectResultStatus;
import com.moa.backend.domain.project.repository.ProjectRepository;
import com.moa.backend.domain.settlement.entity.Settlement;
import com.moa.backend.domain.settlement.entity.SettlementStatus;
import com.moa.backend.domain.settlement.repository.SettlementRepository;
import com.moa.backend.domain.user.repository.UserRepository;
import com.moa.backend.domain.wallet.entity.PlatformWalletTransactionType;
import com.moa.backend.domain.wallet.repository.PlatformWalletTransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatisticsServiceImpl implements StatisticsService {

    private final OrderRepository orderRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final MakerRepository makerRepository;
    private final SettlementRepository settlementRepository;
    private final PaymentRepository paymentRepository;
    private final PlatformWalletTransactionRepository platformWalletTransactionRepository;
    private final RefundRepository refundRepository;

    // 수수료율 상수
    private static final BigDecimal PG_FEE_RATE = new BigDecimal("0.05");  // 5%
    private static final BigDecimal PLATFORM_FEE_RATE = new BigDecimal("0.10");  // 10%
    private static final double ALERT_SUCCESS_RATE_THRESHOLD = 80.0;  // 결제 성공률 임계치 (%)
    private static final double ALERT_CANCEL_RATE_THRESHOLD = 5.0;    // 취소/환불률 임계치 (%)

    @Override
    public DashboardSummaryDto getDashboardSummary() {
        log.info("대시보드 통계 조회 시작");

        // 이번 달 기간 계산
        LocalDate now = LocalDate.now();
        LocalDate startOfMonth = now.withDayOfMonth(1);
        LocalDate endOfMonth = now.withDayOfMonth(now.lengthOfMonth());
        LocalDateTime startDateTime = startOfMonth.atStartOfDay();
        LocalDateTime endDateTime = endOfMonth.atTime(23, 59, 59);

        // 저번 달 기간 계산
        LocalDate lastMonthStart = startOfMonth.minusMonths(1);
        LocalDate lastMonthEnd = lastMonthStart.withDayOfMonth(lastMonthStart.lengthOfMonth());
        LocalDateTime lastMonthStartDateTime = lastMonthStart.atStartOfDay();
        LocalDateTime lastMonthEndDateTime = lastMonthEnd.atTime(23, 59, 59);

        // 1. KPI 계산
        KpiSummaryDto kpiSummary = calculateKpiSummary(
                startDateTime, endDateTime,
                lastMonthStartDateTime, lastMonthEndDateTime
        );

        // 2. 트렌드 차트 (이번 달 일별)
        TrendChartDto trendChart = calculateTrendChart(startDateTime, endDateTime);

        // 3. 카테고리별 성과 Top 4
        CategoryPerformanceDto categoryPerformance = calculateCategoryPerformance(startDateTime, endDateTime);

        // 4. Top 프로젝트 5개
        List<TopProjectDto> topProjects = calculateTopProjects(startDateTime, endDateTime);

        // 5. 알림 생성
        List<AlertDto> alerts = generateAlerts(startDateTime, endDateTime);

        return DashboardSummaryDto.builder()
                .kpiSummary(kpiSummary)
                .trendChart(trendChart)
                .categoryPerformance(categoryPerformance)
                .topProjects(topProjects)
                .alerts(alerts)
                .build();
    }

    /**
     * 일일 통계 조회
     */
    @Override
    public DailyStatisticsDto getDailyStatistics(LocalDate startDate, LocalDate endDate, String filterType, String filterValue) {
        validateDateRange(startDate, endDate);

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        Category categoryFilter = parseCategory(filterType, filterValue);
        Long makerIdFilter = parseMakerId(filterType, filterValue);

        TrafficDto traffic = buildTraffic(startDateTime, endDateTime);
        ProjectActivityDto projectActivity = buildProjectActivity(startDate, endDate, startDateTime, endDateTime);
        PaymentStatisticsDto paymentStatistics = buildPaymentStatistics(startDateTime, endDateTime);
        HourlyChartDto hourlyChart = buildHourlyChart(startDateTime, endDateTime, categoryFilter, makerIdFilter);
        List<ProjectDetailDto> projectDetails = buildProjectDetails(startDateTime, endDateTime, categoryFilter, makerIdFilter);
        List<MakerDetailDto> makerDetails = buildMakerDetails(startDateTime, endDateTime, categoryFilter, makerIdFilter);

        return DailyStatisticsDto.builder()
                .traffic(traffic)
                .projectActivity(projectActivity)
                .paymentStatistics(paymentStatistics)
                .hourlyChart(hourlyChart)
                .projectDetails(projectDetails)
                .makerDetails(makerDetails)
                .build();
    }

    private void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("startDate와 endDate는 필수입니다.");
        }
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("startDate가 endDate보다 이후일 수 없습니다.");
        }
    }

    private Category parseCategory(String filterType, String filterValue) {
        if (!"CATEGORY".equalsIgnoreCase(filterType) || filterValue == null) {
            return null;
        }
        try {
            return Category.valueOf(filterValue);
        } catch (IllegalArgumentException e) {
            log.warn("잘못된 카테고리 값: {}", filterValue);
            return null;
        }
    }

    private Long parseMakerId(String filterType, String filterValue) {
        if (!"MAKER".equalsIgnoreCase(filterType) || filterValue == null) {
            return null;
        }
        try {
            return Long.valueOf(filterValue);
        } catch (NumberFormatException e) {
            log.warn("잘못된 메이커 ID 값: {}", filterValue);
            return null;
        }
    }

    private TrafficDto buildTraffic(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        Long newUsers = userRepository.countByCreatedAtBetween(startDateTime, endDateTime);
        return TrafficDto.builder()
                .uniqueVisitors(0L)   // 추후 GA 연동
                .pageViews(0L)        // 추후 GA 연동
                .newUsers(newUsers)
                .returningRate(0.0)   // 추후 GA 연동
                .build();
    }

    private ProjectActivityDto buildProjectActivity(LocalDate startDate, LocalDate endDate, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        Long newProjects = projectRepository.countByCreatedAtBetween(startDateTime, endDateTime);
        Long reviewRequested = projectRepository.countByReviewStatusAndCreatedAtBetween(
                ProjectReviewStatus.REVIEW, startDateTime, endDateTime);
        Long approved = projectRepository.countByReviewStatusAndApprovedAtBetween(
                ProjectReviewStatus.APPROVED, startDateTime, endDateTime);
        Long closed = projectRepository.countByLifecycleStatusAndEndDateBetween(
                ProjectLifecycleStatus.ENDED, startDate, endDate);

        return ProjectActivityDto.builder()
                .newProjectCount(newProjects)
                .reviewRequestedCount(reviewRequested)
                .approvedCount(approved)
                .closedTodayCount(closed)
                .build();
    }

    private PaymentStatisticsDto buildPaymentStatistics(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        Long attemptCount = paymentRepository.countByCreatedAtBetween(startDateTime, endDateTime);
        Long successCount = paymentRepository.countByStatusAndCreatedAtBetween(
                PaymentStatus.DONE, startDateTime, endDateTime);
        Long failureCount = paymentRepository.countByStatusAndCreatedAtBetween(
                PaymentStatus.CANCELED, startDateTime, endDateTime);

        Long refundCount = refundRepository.countByStatusAndCreatedAtBetween(
                RefundStatus.COMPLETED, startDateTime, endDateTime);
        Long refundAmount = refundRepository.sumAmountByStatusAndCreatedAtBetween(
                RefundStatus.COMPLETED, startDateTime, endDateTime
        ).orElse(0L);

        double successRate = attemptCount == 0 ? 0.0 :
                Math.round((successCount * 100.0 / attemptCount) * 10.0) / 10.0;

        return PaymentStatisticsDto.builder()
                .attemptCount(attemptCount)
                .successCount(successCount)
                .successRate(successRate)
                .failureCount(failureCount)
                .refundCount(refundCount)
                .refundAmount(refundAmount)
                .build();
    }

    private HourlyChartDto buildHourlyChart(LocalDateTime startDateTime, LocalDateTime endDateTime, Category category, Long makerId) {
        List<Object[]> successRows = orderRepository.findHourlyStatsByStatusAndFilters(
                startDateTime, endDateTime, OrderStatus.PAID, category, makerId);
        List<Object[]> failureRows = orderRepository.findHourlyStatsByStatusAndFilters(
                startDateTime, endDateTime, OrderStatus.CANCELED, category, makerId);

        Map<Integer, HourlyDataDto> hourlyMap = new HashMap<>();
        for (int h = 0; h <= 23; h++) {
            hourlyMap.put(h, HourlyDataDto.builder()
                    .hour(h)
                    .successCount(0)
                    .failureCount(0)
                    .successAmount(0L)
                    .build());
        }

        successRows.forEach(row -> {
            Integer hour = ((Number) row[0]).intValue();
            Long count = ((Number) row[1]).longValue();
            Long amount = ((Number) row[2]).longValue();
            HourlyDataDto existing = hourlyMap.get(hour);
            hourlyMap.put(hour, HourlyDataDto.builder()
                    .hour(hour)
                    .successCount(existing.getSuccessCount() + count.intValue())
                    .failureCount(existing.getFailureCount())
                    .successAmount(existing.getSuccessAmount() + amount)
                    .build());
        });

        failureRows.forEach(row -> {
            Integer hour = ((Number) row[0]).intValue();
            Long count = ((Number) row[1]).longValue();
            HourlyDataDto existing = hourlyMap.get(hour);
            hourlyMap.put(hour, HourlyDataDto.builder()
                    .hour(hour)
                    .successCount(existing.getSuccessCount())
                    .failureCount(existing.getFailureCount() + count.intValue())
                    .successAmount(existing.getSuccessAmount())
                    .build());
        });

        List<HourlyDataDto> data = hourlyMap.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(Map.Entry::getValue)
                .toList();

        return HourlyChartDto.builder()
                .data(data)
                .build();
    }

    private List<ProjectDetailDto> buildProjectDetails(LocalDateTime startDateTime, LocalDateTime endDateTime, Category category, Long makerId) {
        List<Object[]> rows = orderRepository.findProjectDetailsByStatusAndFilters(
                startDateTime, endDateTime, OrderStatus.PAID, category, makerId);

        return rows.stream()
                .map(row -> {
                    Long projectId = ((Number) row[0]).longValue();
                    String projectName = (String) row[1];
                    String makerName = (String) row[2];
                    Integer orderCount = ((Number) row[3]).intValue();
                    Long fundingAmount = ((Number) row[4]).longValue();

                    // 방문자 데이터가 없으므로 전환율은 0.0으로 반환
                    return ProjectDetailDto.builder()
                            .projectId(projectId)
                            .projectName(projectName)
                            .makerName(makerName)
                            .orderCount(orderCount)
                            .fundingAmount(fundingAmount)
                            .conversionRate(0.0)
                            .build();
                })
                .toList();
    }

    private List<MakerDetailDto> buildMakerDetails(LocalDateTime startDateTime, LocalDateTime endDateTime, Category category, Long makerId) {
        List<Object[]> rows = orderRepository.findMakerDetailsByStatusAndFilters(
                startDateTime, endDateTime, OrderStatus.PAID, category, makerId);

        return rows.stream()
                .map(row -> {
                    Long makerIdVal = ((Number) row[0]).longValue();
                    String makerName = (String) row[1];
                    Integer projectCount = ((Number) row[2]).intValue();
                    Integer orderCount = ((Number) row[3]).intValue();
                    Long fundingAmount = ((Number) row[4]).longValue();

                    return MakerDetailDto.builder()
                            .makerId(makerIdVal)
                            .makerName(makerName)
                            .projectCount(projectCount)
                            .orderCount(orderCount)
                            .fundingAmount(fundingAmount)
                            .build();
                })
                .toList();
    }

    /**
     * KPI 6개 계산
     */
    private KpiSummaryDto calculateKpiSummary(
            LocalDateTime startDateTime, LocalDateTime endDateTime,
            LocalDateTime lastMonthStartDateTime, LocalDateTime lastMonthEndDateTime) {

        // 이번 달 PAID 주문 총액
        Long currentFundingAmount = orderRepository
                .sumTotalAmountByStatusAndCreatedAtBetween(
                        OrderStatus.PAID,
                        startDateTime,
                        endDateTime
                ).orElse(0L);

        // 저번 달 PAID 주문 총액
        Long lastFundingAmount = orderRepository
                .sumTotalAmountByStatusAndCreatedAtBetween(
                        OrderStatus.PAID,
                        lastMonthStartDateTime,
                        lastMonthEndDateTime
                ).orElse(0L);

        // 이번 달 PAID 주문 건수
        Long currentOrderCount = orderRepository
                .countByStatusAndCreatedAtBetween(
                        OrderStatus.PAID,
                        startDateTime,
                        endDateTime
                );

        // 저번 달 PAID 주문 건수
        Long lastOrderCount = orderRepository
                .countByStatusAndCreatedAtBetween(
                        OrderStatus.PAID,
                        lastMonthStartDateTime,
                        lastMonthEndDateTime
                );

        // 플랫폼 수수료 수익: 지갑 트랜잭션 로그 기준 (PLATFORM_FEE_IN만 합산)
        Long platformFeeRevenue = platformWalletTransactionRepository
                .sumAmountByTypeAndCreatedAtBetween(
                        PlatformWalletTransactionType.PLATFORM_FEE_IN,
                        startDateTime,
                        endDateTime
                ).orElse(0L);

        Long lastPlatformFeeRevenue = platformWalletTransactionRepository
                .sumAmountByTypeAndCreatedAtBetween(
                        PlatformWalletTransactionType.PLATFORM_FEE_IN,
                        lastMonthStartDateTime,
                        lastMonthEndDateTime
                ).orElse(0L);

        // 신규 프로젝트 수
        Long currentNewProjects = projectRepository
                .countByCreatedAtBetween(startDateTime, endDateTime);

        Long lastNewProjects = projectRepository
                .countByCreatedAtBetween(lastMonthStartDateTime, lastMonthEndDateTime);

        // 신규 가입자 수
        Long currentNewUsers = userRepository
                .countByCreatedAtBetween(startDateTime, endDateTime);

        Long lastNewUsers = userRepository
                .countByCreatedAtBetween(lastMonthStartDateTime, lastMonthEndDateTime);

        // 활성 서포터 수 (전체 기간 동안 PAID 주문이 있는 서포터)
        Long activeSupporters = orderRepository
                .countDistinctUserByStatus(OrderStatus.PAID);

        Long lastActiveSupporters = orderRepository
                .countDistinctUserByStatusAndCreatedAtBefore(OrderStatus.PAID, lastMonthEndDateTime);

        return KpiSummaryDto.builder()
                .totalFundingAmount(buildKpiItem(currentFundingAmount, lastFundingAmount))
                .totalOrderCount(buildKpiItem(currentOrderCount, lastOrderCount))
                .platformFeeRevenue(buildKpiItem(platformFeeRevenue, lastPlatformFeeRevenue))
                .newProjectCount(buildKpiItem(currentNewProjects, lastNewProjects))
                .newUserCount(buildKpiItem(currentNewUsers, lastNewUsers))
                .activeSupporterCount(buildKpiItem(activeSupporters, lastActiveSupporters))
                .build();
    }

    /**
     * KpiItemDto 생성 헬퍼
     */
    private KpiItemDto buildKpiItem(Long currentValue, Long lastValue) {
        Long changeAmount = currentValue - lastValue;
        Double changeRate = lastValue == 0 ? 0.0 :
                ((double) changeAmount / lastValue) * 100.0;

        return KpiItemDto.builder()
                .value(currentValue)
                .changeRate(Math.round(changeRate * 10.0) / 10.0)  // 소수점 1자리
                .changeAmount(changeAmount)
                .build();
    }

    /**
     * 트렌드 차트 계산 (일별)
     */
    private TrendChartDto calculateTrendChart(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        List<Object[]> dailyStats = orderRepository.findDailyStatsByStatusAndCreatedAtBetween(
                OrderStatus.PAID,
                startDateTime,
                endDateTime
        );

        // 일별 프로젝트 수 (주문이 발생한 고유 프로젝트 기준)
        List<Object[]> dailyProjectStats = orderRepository.findDailyProjectCountByStatusAndCreatedAtBetween(
                OrderStatus.PAID,
                startDateTime,
                endDateTime
        );
        Map<LocalDate, Integer> projectCountMap = new HashMap<>();
        for (Object[] row : dailyProjectStats) {
            java.sql.Date sqlDate = (java.sql.Date) row[0];
            LocalDate date = sqlDate.toLocalDate();
            Integer projectCount = ((Number) row[1]).intValue();
            projectCountMap.put(date, projectCount);
        }

        List<TrendDataDto> trendData = dailyStats.stream()
                .map(row -> {
                    // Object[] {날짜(DATE), 총액(LONG), 건수(LONG)}
                    java.sql.Date sqlDate = (java.sql.Date) row[0];
                    LocalDate date = sqlDate.toLocalDate();
                    Long totalAmount = ((Number) row[1]).longValue();
                    Long orderCount = ((Number) row[2]).longValue();

                    Integer projectCount = projectCountMap.getOrDefault(date, 0);

                    return TrendDataDto.builder()
                            .date(String.format("%02d/%02d", date.getMonthValue(), date.getDayOfMonth()))
                            .fundingAmount(totalAmount)
                            .projectCount(projectCount)
                            .orderCount(orderCount.intValue())
                            .build();
                })
                .toList();

        return TrendChartDto.builder()
                .data(trendData)
                .build();
    }

    /**
     * 카테고리별 성과 Top 4
     */
    private CategoryPerformanceDto calculateCategoryPerformance(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        List<Object[]> categoryStats = orderRepository.findCategoryStatsByStatusAndCreatedAtBetween(
                OrderStatus.PAID,
                startDateTime,
                endDateTime
        );

        // 전체 펀딩액 계산 (비율 계산용)
        Long totalFundingAmount = categoryStats.stream()
                .mapToLong(row -> ((Number) row[1]).longValue())
                .sum();

        // Top 4만 추출
        List<CategoryItemDto> categories = categoryStats.stream()
                .limit(4)
                .map(row -> {
                    // Object[] {카테고리(Enum), 총액(LONG), 프로젝트수(LONG), 주문건수(LONG)}
                    var categoryEnum = (com.moa.backend.domain.project.entity.Category) row[0];
                    String category = categoryEnum.name();
                    Long fundingAmount = ((Number) row[1]).longValue();
                    Long projectCount = ((Number) row[2]).longValue();
                    Long orderCount = ((Number) row[3]).longValue();

                    // 비율 계산
                    Double fundingRatio = totalFundingAmount == 0 ? 0.0 :
                            (fundingAmount * 100.0 / totalFundingAmount);

                    return CategoryItemDto.builder()
                            .categoryName(category)
                            .fundingAmount(fundingAmount)
                            .projectCount(projectCount.intValue())
                            .orderCount(orderCount.intValue())
                            .fundingRatio(Math.round(fundingRatio * 10.0) / 10.0)  // 소수점 1자리
                            .build();
                })
                .toList();

        return CategoryPerformanceDto.builder()
                .categories(categories)
                .build();
    }

    /**
     * Top 프로젝트 5개
     */
    private List<TopProjectDto> calculateTopProjects(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        Pageable pageable = org.springframework.data.domain.PageRequest.of(0, 5);

        List<Object[]> topProjectsData = orderRepository.findTopProjectsByFundingAmount(
                OrderStatus.PAID,
                startDateTime,
                endDateTime,
                pageable
        );

        return topProjectsData.stream()
                .map(row -> {
                    // Object[] {프로젝트ID, 프로젝트명, 메이커명, 총펀딩액, 목표금액, 달성률, 남은일수}
                    Long projectId = ((Number) row[0]).longValue();
                    String projectName = (String) row[1];
                    String makerName = (String) row[2];
                    Long fundingAmount = ((Number) row[3]).longValue();
                    Long goalAmount = ((Number) row[4]).longValue();
                    Double achievementRate = ((Number) row[5]).doubleValue();
                    Integer remainingDays = ((Number) row[6]).intValue();

                    return TopProjectDto.builder()
                            .projectId(projectId)
                            .projectName(projectName)
                            .makerName(makerName)
                            .achievementRate(Math.round(achievementRate * 10.0) / 10.0)  // 소수점 1자리
                            .fundingAmount(fundingAmount)
                            .remainingDays(remainingDays)
                            .build();
                })
                .toList();
    }

    /**
     * 알림 생성
     */
    private List<AlertDto> generateAlerts(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        List<AlertDto> alerts = new ArrayList<>();

        long paidCount = orderRepository.countByStatusAndCreatedAtBetween(
                OrderStatus.PAID, startDateTime, endDateTime);
        long canceledCount = orderRepository.countByStatusAndCreatedAtBetween(
                OrderStatus.CANCELED, startDateTime, endDateTime);

        long attempts = paidCount + canceledCount;
        if (attempts > 0) {
            double successRate = (paidCount * 100.0) / attempts;
            if (successRate < ALERT_SUCCESS_RATE_THRESHOLD) {
                alerts.add(AlertDto.builder()
                        .type("WARNING")
                        .title("결제 성공률 급감")
                        .message(String.format("이번 달 결제 성공률이 %.1f%%로 %.0f%% 미만입니다. PG 상태를 확인해주세요.", successRate, ALERT_SUCCESS_RATE_THRESHOLD))
                        .build());
            }

            double cancelRate = (canceledCount * 100.0) / attempts;
            if (cancelRate > ALERT_CANCEL_RATE_THRESHOLD) {
                alerts.add(AlertDto.builder()
                        .type("INFO")
                        .title("환불/취소 비율 증가")
                        .message(String.format("이번 달 환불·취소 비율이 %.1f%%로 %.0f%%를 초과했습니다.", cancelRate, ALERT_CANCEL_RATE_THRESHOLD))
                        .build());
            }
        }

        return alerts;
    }

    /**
     * 수익 리포트 조회
     */
    @Override
    public RevenueReportDto getRevenueReport(LocalDate startDate, LocalDate endDate, Long makerId, Long projectId) {
        validateDateRange(startDate, endDate);

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        PlatformRevenueDto platformRevenue = buildPlatformRevenue(startDateTime, endDateTime, makerId, projectId);
        MakerSettlementSummaryDto makerSettlementSummary = buildMakerSettlementSummary(startDateTime, endDateTime, makerId, projectId);
        FeePolicyAnalysisDto feePolicyAnalysis = buildFeePolicyAnalysis(startDateTime, endDateTime, makerId, projectId, platformRevenue);
        List<RevenueDetailDto> details = buildRevenueDetails(startDateTime, endDateTime, makerId, projectId);

        return RevenueReportDto.builder()
                .platformRevenue(platformRevenue)
                .makerSettlementSummary(makerSettlementSummary)
                .feePolicyAnalysis(feePolicyAnalysis)
                .details(details)
                .build();
    }

    private PlatformRevenueDto buildPlatformRevenue(LocalDateTime startDateTime, LocalDateTime endDateTime, Long makerId, Long projectId) {
        Long totalPaymentAmount = orderRepository
                .sumTotalAmountByStatusAndCreatedAtBetweenAndFilters(
                        OrderStatus.PAID, startDateTime, endDateTime, makerId, projectId)
                .orElse(0L);

        Long pgFeeAmount = BigDecimal.valueOf(totalPaymentAmount)
                .multiply(PG_FEE_RATE)
                .setScale(0, RoundingMode.DOWN)
                .longValue();

        Long platformFeeAmount = BigDecimal.valueOf(totalPaymentAmount)
                .multiply(PLATFORM_FEE_RATE)
                .setScale(0, RoundingMode.DOWN)
                .longValue();

        Long otherCosts = 0L; // TODO: 기타 비용 정의 시 반영
        // 메이커 지급액(총 결제액 - PG 수수료 - 플랫폼 수수료)
        Long netPayoutToMaker = totalPaymentAmount - pgFeeAmount - platformFeeAmount - otherCosts;
        // 플랫폼 순이익(플랫폼 수익 = 플랫폼 수수료 10%) - PG 비용을 제외하지 않음
        Long netPlatformProfit = platformFeeAmount;

        return PlatformRevenueDto.builder()
                .totalPaymentAmount(totalPaymentAmount)
                .pgFeeAmount(pgFeeAmount)
                .pgFeeRate(PG_FEE_RATE.multiply(BigDecimal.valueOf(100)).doubleValue())
                .platformFeeAmount(platformFeeAmount)
                .platformFeeRate(PLATFORM_FEE_RATE.multiply(BigDecimal.valueOf(100)).doubleValue())
                .otherCosts(otherCosts)
                .netPayoutToMaker(netPayoutToMaker)
                .netPlatformProfit(netPlatformProfit)
                .build();
    }

    private MakerSettlementSummaryDto buildMakerSettlementSummary(LocalDateTime startDateTime, LocalDateTime endDateTime, Long makerId, Long projectId) {
        Long totalSettlementAmount = settlementRepository.sumNetAmountByCreatedAtBetweenAndFilters(
                startDateTime, endDateTime, makerId, projectId).orElse(0L);

        Long pendingAmount = settlementRepository.sumNetAmountByStatusAndCreatedAtBetweenAndFilters(
                SettlementStatus.PENDING, startDateTime, endDateTime, makerId, projectId).orElse(0L);

        Long processingAmount = settlementRepository.sumNetAmountByStatusAndCreatedAtBetweenAndFilters(
                SettlementStatus.FIRST_PAID, startDateTime, endDateTime, makerId, projectId).orElse(0L)
                + settlementRepository.sumNetAmountByStatusAndCreatedAtBetweenAndFilters(
                        SettlementStatus.FINAL_READY, startDateTime, endDateTime, makerId, projectId).orElse(0L);

        Long completedAmount = settlementRepository.sumNetAmountByStatusAndCreatedAtBetweenAndFilters(
                SettlementStatus.COMPLETED, startDateTime, endDateTime, makerId, projectId).orElse(0L);

        return MakerSettlementSummaryDto.builder()
                .totalSettlementAmount(totalSettlementAmount)
                .pendingAmount(pendingAmount)
                .processingAmount(processingAmount)
                .completedAmount(completedAmount)
                .build();
    }

    private FeePolicyAnalysisDto buildFeePolicyAnalysis(LocalDateTime startDateTime, LocalDateTime endDateTime, Long makerId, Long projectId, PlatformRevenueDto platformRevenueDto) {
        Long projectCount = orderRepository.countDistinctProjectByStatusAndCreatedAtBetweenAndFilters(
                OrderStatus.PAID, startDateTime, endDateTime, makerId, projectId);

        FeePolicyItemDto generalPolicy = FeePolicyItemDto.builder()
                .policyName("일반 프로젝트 (10%)")
                .projectCount(projectCount.intValue())
                .paymentAmount(platformRevenueDto.getTotalPaymentAmount())
                .feeAmount(platformRevenueDto.getPlatformFeeAmount())
                .contributionRate(100.0)
                .build();

        return FeePolicyAnalysisDto.builder()
                .policies(List.of(generalPolicy))
                .build();
    }

    private List<RevenueDetailDto> buildRevenueDetails(LocalDateTime startDateTime, LocalDateTime endDateTime, Long makerId, Long projectId) {
        List<Object[]> rows = orderRepository.findRevenueDetailsByDateAndFilters(
                OrderStatus.PAID, startDateTime, endDateTime, makerId, projectId);

        List<Long> projectIds = rows.stream()
                .map(row -> ((Number) row[1]).longValue())
                .distinct()
                .toList();
        Map<Long, SettlementStatus> settlementStatusMap = settlementRepository.findByProjectIdIn(projectIds).stream()
                .collect(Collectors.toMap(s -> s.getProject().getId(), Settlement::getStatus));

        return rows.stream()
                .map(row -> {
                    java.sql.Date sqlDate = (java.sql.Date) row[0];
                    String dateStr = sqlDate.toLocalDate().toString();
                    Long projectIdVal = ((Number) row[1]).longValue();
                    String projectName = (String) row[2];
                    String makerName = (String) row[3];
                    Long paymentAmount = ((Number) row[4]).longValue();

                    Long pgFee = BigDecimal.valueOf(paymentAmount)
                            .multiply(PG_FEE_RATE)
                            .setScale(0, RoundingMode.DOWN)
                            .longValue();
                    Long platformFee = BigDecimal.valueOf(paymentAmount)
                            .multiply(PLATFORM_FEE_RATE)
                            .setScale(0, RoundingMode.DOWN)
                            .longValue();
                    Long makerSettlementAmount = paymentAmount - pgFee - platformFee;

                    String settlementStatus = settlementStatusMap.getOrDefault(projectIdVal, SettlementStatus.PENDING).name();

                    return RevenueDetailDto.builder()
                            .date(dateStr)
                            .projectId(projectIdVal)
                            .projectName(projectName)
                            .makerName(makerName)
                            .paymentAmount(paymentAmount)
                            .pgFee(pgFee)
                            .platformFee(platformFee)
                            .makerSettlementAmount(makerSettlementAmount)
                            .settlementStatus(settlementStatus)
                            .build();
                })
                .toList();
    }

    /**
     * 월별 리포트 조회
     */
    @Override
    public MonthlyReportDto getMonthlyReport(String targetMonth, String compareMonth) {
        LocalDate target = parseMonth(targetMonth);
        LocalDate compare = (compareMonth == null || compareMonth.isBlank())
                ? target.minusMonths(1)
                : parseMonth(compareMonth);

        LocalDateTime targetStart = target.withDayOfMonth(1).atStartOfDay();
        LocalDateTime targetEnd = target.withDayOfMonth(target.lengthOfMonth()).atTime(23, 59, 59);
        LocalDateTime compareStart = compare.withDayOfMonth(1).atStartOfDay();
        LocalDateTime compareEnd = compare.withDayOfMonth(compare.lengthOfMonth()).atTime(23, 59, 59);

        MonthlyKpiDto kpi = buildMonthlyKpi(targetStart, targetEnd, compareStart, compareEnd);
        MonthlyTrendChartDto trendChart = buildMonthlyTrendChart(targetStart, targetEnd);
        SuccessRateDto successRate = buildSuccessRate(target);
        GoalAmountRangeDto goalAmountRange = buildGoalAmountRange(target);
        CategorySuccessRateDto categorySuccessRate = buildCategorySuccessRate(targetStart, targetEnd);
        RetentionDto retention = buildRetention(targetStart, targetEnd);

        return MonthlyReportDto.builder()
                .targetMonth(formatMonth(target))
                .compareMonth(formatMonth(compare))
                .kpi(kpi)
                .trendChart(trendChart)
                .successRate(successRate)
                .goalAmountRange(goalAmountRange)
                .categorySuccessRate(categorySuccessRate)
                .retention(retention)
                .build();
    }

    private LocalDate parseMonth(String monthStr) {
        try {
            return LocalDate.parse(monthStr + "-01");
        } catch (Exception e) {
            throw new IllegalArgumentException("targetMonth/compareMonth는 yyyy-MM 형식이어야 합니다.");
        }
    }

    private String formatMonth(LocalDate month) {
        return String.format("%d-%02d", month.getYear(), month.getMonthValue());
    }

    private MonthlyKpiDto buildMonthlyKpi(LocalDateTime targetStart, LocalDateTime targetEnd,
                                          LocalDateTime compareStart, LocalDateTime compareEnd) {
        List<Object[]> target = orderRepository.findMonthlyFundingAndCount(OrderStatus.PAID, targetStart, targetEnd);
        List<Object[]> compare = orderRepository.findMonthlyFundingAndCount(OrderStatus.PAID, compareStart, compareEnd);

        Long targetAmount = target.isEmpty() ? 0L : ((Number) target.get(0)[0]).longValue();
        Long compareAmount = compare.isEmpty() ? 0L : ((Number) compare.get(0)[0]).longValue();

        KpiItemDto totalFunding = buildKpiItem(targetAmount, compareAmount);

        Long successProjectCount = projectRepository.countByResultStatusAndEndDateBetween(
                com.moa.backend.domain.project.entity.ProjectResultStatus.SUCCESS,
                targetStart.toLocalDate(),
                targetEnd.toLocalDate()
        );
        Long failedProjectCount = projectRepository.countByResultStatusAndEndDateBetween(
                com.moa.backend.domain.project.entity.ProjectResultStatus.FAILED,
                targetStart.toLocalDate(),
                targetEnd.toLocalDate()
        );

        Long newMakerCount = makerRepository.countByCreatedAtBetween(targetStart, targetEnd);
        Long newSupporterCount = userRepository.countByCreatedAtBetween(targetStart, targetEnd);

        return MonthlyKpiDto.builder()
                .totalFundingAmount(totalFunding)
                .successProjectCount(successProjectCount.intValue())
                .failedProjectCount(failedProjectCount.intValue())
                .newMakerCount(newMakerCount.intValue())
                .newSupporterCount(newSupporterCount.intValue())
                .build();
    }

    private MonthlyTrendChartDto buildMonthlyTrendChart(LocalDateTime targetStart, LocalDateTime targetEnd) {
        List<Object[]> rows = orderRepository.findMonthlyDailyStats(OrderStatus.PAID, targetStart, targetEnd);
        List<MonthlyTrendDataDto> data = rows.stream()
                .map(row -> {
                    java.sql.Date sqlDate = (java.sql.Date) row[0];
                    LocalDate date = sqlDate.toLocalDate();
                    Long fundingAmount = ((Number) row[1]).longValue();
                    Integer orderCount = ((Number) row[2]).intValue();
                    Integer projectCount = ((Number) row[3]).intValue();

                    return MonthlyTrendDataDto.builder()
                            .date(String.format("%02d/%02d", date.getMonthValue(), date.getDayOfMonth()))
                            .fundingAmount(fundingAmount)
                            .projectCount(projectCount)
                            .orderCount(orderCount)
                            .build();
                })
                .toList();

        return MonthlyTrendChartDto.builder()
                .data(data)
                .build();
    }

    private SuccessRateDto buildSuccessRate(LocalDate targetMonth) {
        LocalDate start = targetMonth.withDayOfMonth(1);
        LocalDate end = targetMonth.withDayOfMonth(targetMonth.lengthOfMonth());

        Long startTotal = projectRepository.countByStartDateBetween(start, end);
        Long startSuccess = projectRepository.countByStartDateBetweenAndResultStatus(
                start, end, com.moa.backend.domain.project.entity.ProjectResultStatus.SUCCESS);

        Long endTotal = projectRepository.countByEndDateBetween(start, end);
        Long endSuccess = projectRepository.countByEndDateBetweenAndResultStatus(
                start, end, com.moa.backend.domain.project.entity.ProjectResultStatus.SUCCESS);

        SuccessRateItemDto startBased = SuccessRateItemDto.builder()
                .successCount(startSuccess.intValue())
                .totalCount(startTotal.intValue())
                .rate(startTotal == 0 ? 0.0 : Math.round((startSuccess * 100.0 / startTotal) * 10.0) / 10.0)
                .build();

        SuccessRateItemDto endBased = SuccessRateItemDto.builder()
                .successCount(endSuccess.intValue())
                .totalCount(endTotal.intValue())
                .rate(endTotal == 0 ? 0.0 : Math.round((endSuccess * 100.0 / endTotal) * 10.0) / 10.0)
                .build();

        return SuccessRateDto.builder()
                .startBased(startBased)
                .endBased(endBased)
                .build();
    }

    private GoalAmountRangeDto buildGoalAmountRange(LocalDate targetMonth) {
        LocalDate start = targetMonth.withDayOfMonth(1);
        LocalDate end = targetMonth.withDayOfMonth(targetMonth.lengthOfMonth());

        List<GoalRangeItemDto> ranges = new ArrayList<>();
        ranges.add(calculateGoalRange(start, end, 0L, 999_999L, "소액 (100만원 미만)"));
        ranges.add(calculateGoalRange(start, end, 1_000_000L, 9_999_999L, "중간 (100~1000만원)"));
        ranges.add(calculateGoalRange(start, end, 10_000_000L, Long.MAX_VALUE, "고액 (1000만원 이상)"));

        return GoalAmountRangeDto.builder()
                .ranges(ranges)
                .build();
    }

    private GoalRangeItemDto calculateGoalRange(LocalDate start, LocalDate end, Long minGoal, Long maxGoal, String name) {
        Long total = projectRepository.countByGoalAmountBetweenAndEndDateBetween(minGoal, maxGoal, start, end);
        Long success = projectRepository.countByGoalAmountBetweenAndEndDateBetweenAndResultStatus(
                minGoal, maxGoal, start, end, com.moa.backend.domain.project.entity.ProjectResultStatus.SUCCESS);

        double rate = total == 0 ? 0.0 : Math.round((success * 100.0 / total) * 10.0) / 10.0;

        return GoalRangeItemDto.builder()
                .rangeName(name)
                .totalCount(total.intValue())
                .successCount(success.intValue())
                .successRate(rate)
                .build();
    }

    private CategorySuccessRateDto buildCategorySuccessRate(LocalDateTime targetStart, LocalDateTime targetEnd) {
        List<CategorySuccessItemDto> items = new ArrayList<>();
        for (Category category : Category.values()) {
            Long total = projectRepository.countByCategoryAndEndDateBetween(category, targetStart.toLocalDate(), targetEnd.toLocalDate());
            Long success = projectRepository.countByCategoryAndEndDateBetweenAndResultStatus(
                    category, targetStart.toLocalDate(), targetEnd.toLocalDate(),
                    com.moa.backend.domain.project.entity.ProjectResultStatus.SUCCESS);

            double rate = total == 0 ? 0.0 : Math.round((success * 100.0 / total) * 10.0) / 10.0;

            items.add(CategorySuccessItemDto.builder()
                    .categoryName(category.name())
                    .totalCount(total.intValue())
                    .successCount(success.intValue())
                    .successRate(rate)
                    .build());
        }

        return CategorySuccessRateDto.builder()
                .categories(items)
                .build();
    }

    private RetentionDto buildRetention(LocalDateTime targetStart, LocalDateTime targetEnd) {
        Long totalSupporters = orderRepository.countDistinctSupporterByStatusAndCreatedAtBetween(
                OrderStatus.PAID, targetStart, targetEnd);

        Long newSupporters = userRepository.countByCreatedAtBetween(targetStart, targetEnd);
        long existingSupporters = Math.max(totalSupporters - newSupporters, 0);

        double repeatRate = totalSupporters == 0 ? 0.0 : Math.round((existingSupporters * 100.0 / totalSupporters) * 10.0) / 10.0;
        double existingRatio = totalSupporters == 0 ? 0.0 : Math.round((existingSupporters * 100.0 / totalSupporters) * 10.0) / 10.0;
        double newRatio = totalSupporters == 0 ? 0.0 : 100.0 - existingRatio;

        return RetentionDto.builder()
                .repeatSupporterRate(repeatRate)
                .existingSupporterCount(existingSupporters)
                .newSupporterCount(newSupporters)
                .existingRatio(existingRatio)
                .newRatio(newRatio)
                .build();
    }

    /**
     * 프로젝트 성과 리포트 조회
     */
    @Override
    public ProjectPerformanceDto getProjectPerformance(String category, Long makerId) {
        Category categoryFilter = parseCategoryParam(category);

        List<Object[]> rows = projectRepository.findProjectPerformanceStats(
                OrderStatus.PAID, categoryFilter, makerId);

        List<ProjectPerformanceItemDto> projectItems = new ArrayList<>();
        Map<Category, CategoryAgg> categoryAggMap = new HashMap<>();
        Map<Long, MakerAgg> makerAggMap = new HashMap<>();
        List<RiskProjectDto> riskProjects = new ArrayList<>();
        List<OpportunityProjectDto> opportunityProjects = new ArrayList<>();

        LocalDate today = LocalDate.now();

        for (Object[] row : rows) {
            Long projectId = ((Number) row[0]).longValue();
            String projectName = (String) row[1];
            Long makerIdVal = ((Number) row[2]).longValue();
            String makerName = (String) row[3];
            Category cat = (Category) row[4];
            Long goalAmount = row[5] == null ? 0L : ((Number) row[5]).longValue();
            LocalDate endDate = (LocalDate) row[6];
            ProjectResultStatus resultStatus = (ProjectResultStatus) row[7];
            Long fundingAmount = ((Number) row[8]).longValue();
            Integer supporterCount = ((Number) row[9]).intValue();
            Long bookmarkCount = ((Number) row[10]).longValue();

            double achievementRate = goalAmount == null || goalAmount == 0
                    ? 0.0
                    : round1(fundingAmount * 100.0 / goalAmount);
            Long avgSupportAmount = supporterCount == 0 ? 0L : fundingAmount / supporterCount;
            Integer remainingDays = endDate == null ? 0 : (int) ChronoUnit.DAYS.between(today, endDate);

            ProjectPerformanceItemDto item = ProjectPerformanceItemDto.builder()
                    .projectId(projectId)
                    .projectName(projectName)
                    .makerName(makerName)
                    .category(cat.name())
                    .fundingAmount(fundingAmount)
                    .achievementRate(achievementRate)
                    .supporterCount(supporterCount)
                    .averageSupportAmount(avgSupportAmount)
                    .bookmarkCount(bookmarkCount)
                    .conversionRate(0.0) // 방문자 데이터 없음
                    .remainingDays(remainingDays)
                    .build();
            projectItems.add(item);

            // 카테고리 집계
            CategoryAgg cAgg = categoryAggMap.getOrDefault(cat, new CategoryAgg());
            cAgg.totalFunding += fundingAmount;
            cAgg.totalAchievement += achievementRate;
            cAgg.count += 1;
            if (resultStatus == ProjectResultStatus.SUCCESS) {
                cAgg.successCount += 1;
            }
            categoryAggMap.put(cat, cAgg);

            // 메이커 집계
            MakerAgg mAgg = makerAggMap.getOrDefault(makerIdVal, new MakerAgg(makerName));
            mAgg.totalFunding += fundingAmount;
            mAgg.count += 1;
            if (resultStatus == ProjectResultStatus.SUCCESS) {
                mAgg.successCount += 1;
            }
            makerAggMap.put(makerIdVal, mAgg);

            // 위험/기회 프로젝트 분류
            if (remainingDays < 7 && achievementRate < 70.0) {
                riskProjects.add(RiskProjectDto.builder()
                        .projectId(projectId)
                        .projectName(projectName)
                        .makerName(makerName)
                        .reason("마감 임박, 달성률 낮음")
                        .remainingDays(remainingDays)
                        .achievementRate(achievementRate)
                        .build());
            } else if (remainingDays > 14 && achievementRate > 80.0) {
                opportunityProjects.add(OpportunityProjectDto.builder()
                        .projectId(projectId)
                        .projectName(projectName)
                        .makerName(makerName)
                        .reason("초기 반응이 좋은 프로젝트")
                        .remainingDays(remainingDays)
                        .achievementRate(achievementRate)
                        .build());
            }
        }

        List<CategoryAverageDto> categoryAverages = categoryAggMap.entrySet().stream()
                .map(entry -> {
                    CategoryAgg agg = entry.getValue();
                    double avgAchievement = agg.count == 0 ? 0.0 : round1(agg.totalAchievement / agg.count);
                    long avgFunding = agg.count == 0 ? 0L : agg.totalFunding / agg.count;
                    double successRate = agg.count == 0 ? 0.0 : round1(agg.successCount * 100.0 / agg.count);
                    return CategoryAverageDto.builder()
                            .categoryName(entry.getKey().name())
                            .averageAchievementRate(avgAchievement)
                            .averageFundingAmount(avgFunding)
                            .successRate(successRate)
                            .build();
                })
                .toList();

        List<MakerAverageDto> makerAverages = makerAggMap.entrySet().stream()
                .map(entry -> {
                    MakerAgg agg = entry.getValue();
                    int count = agg.count;
                    long avgFunding = count == 0 ? 0L : agg.totalFunding / count;
                    double successRate = count == 0 ? 0.0 : round1(agg.successCount * 100.0 / count);
                    boolean isFirstProject = count == 1; // 단순 기준
                    return MakerAverageDto.builder()
                            .makerId(entry.getKey())
                            .makerName(agg.makerName)
                            .projectCount(count)
                            .averageFundingAmount(avgFunding)
                            .successRate(successRate)
                            .isFirstProject(isFirstProject)
                            .build();
                })
                .toList();

        return ProjectPerformanceDto.builder()
                .projects(projectItems)
                .categoryAverages(categoryAverages)
                .makerAverages(makerAverages)
                .riskProjects(riskProjects)
                .opportunityProjects(opportunityProjects)
                .build();
    }

    private Category parseCategoryParam(String category) {
        if (category == null || category.isBlank()) {
            return null;
        }
        try {
            return Category.valueOf(category);
        } catch (IllegalArgumentException e) {
            log.warn("잘못된 카테고리 값: {}", category);
            return null;
        }
    }

    private double round1(double value) {
        return Math.round(value * 10.0) / 10.0;
    }

    private static class CategoryAgg {
        long totalFunding = 0L;
        double totalAchievement = 0.0;
        int count = 0;
        int successCount = 0;
    }

    private static class MakerAgg {
        String makerName;
        long totalFunding = 0L;
        int count = 0;
        int successCount = 0;

        MakerAgg(String makerName) {
            this.makerName = makerName;
        }
    }
}
