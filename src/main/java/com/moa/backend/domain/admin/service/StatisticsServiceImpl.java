package com.moa.backend.domain.admin.service;

import com.moa.backend.domain.admin.dto.statistics.daily.DailyStatisticsDto;
import com.moa.backend.domain.admin.dto.statistics.daily.HourlyChartDto;
import com.moa.backend.domain.admin.dto.statistics.daily.HourlyDataDto;
import com.moa.backend.domain.admin.dto.statistics.daily.MakerDetailDto;
import com.moa.backend.domain.admin.dto.statistics.daily.PaymentStatisticsDto;
import com.moa.backend.domain.admin.dto.statistics.daily.ProjectActivityDto;
import com.moa.backend.domain.admin.dto.statistics.daily.ProjectDetailDto;
import com.moa.backend.domain.admin.dto.statistics.daily.TrafficDto;
import com.moa.backend.domain.admin.dto.statistics.dashboard.AlertDto;
import com.moa.backend.domain.admin.dto.statistics.dashboard.CategoryItemDto;
import com.moa.backend.domain.admin.dto.statistics.dashboard.CategoryPerformanceDto;
import com.moa.backend.domain.admin.dto.statistics.dashboard.DashboardSummaryDto;
import com.moa.backend.domain.admin.dto.statistics.dashboard.KpiItemDto;
import com.moa.backend.domain.admin.dto.statistics.dashboard.KpiSummaryDto;
import com.moa.backend.domain.admin.dto.statistics.dashboard.TopProjectDto;
import com.moa.backend.domain.admin.dto.statistics.dashboard.TrendChartDto;
import com.moa.backend.domain.admin.dto.statistics.dashboard.TrendDataDto;
import com.moa.backend.domain.order.entity.OrderStatus;
import com.moa.backend.domain.order.repository.OrderRepository;
import com.moa.backend.domain.payment.entity.PaymentStatus;
import com.moa.backend.domain.payment.entity.RefundStatus;
import com.moa.backend.domain.payment.repository.PaymentRepository;
import com.moa.backend.domain.payment.repository.RefundRepository;
import com.moa.backend.domain.project.entity.Category;
import com.moa.backend.domain.project.entity.ProjectLifecycleStatus;
import com.moa.backend.domain.project.entity.ProjectReviewStatus;
import com.moa.backend.domain.project.repository.ProjectRepository;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatisticsServiceImpl implements StatisticsService {

    private final OrderRepository orderRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
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
                    // Object[] {카테고리(STRING), 총액(LONG), 프로젝트수(LONG), 주문건수(LONG)}
                    String category = (String) row[0];
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

    // TODO: Phase 3에서 구현
    // @Override
    // public DailyStatisticsDto getDailyStatistics(...) { }

    // TODO: Phase 4에서 구현
    // @Override
    // public RevenueReportDto getRevenueReport(...) { }

    // TODO: Phase 5에서 구현
    // @Override
    // public MonthlyReportDto getMonthlyReport(...) { }

    // TODO: Phase 6에서 구현
    // @Override
    // public ProjectPerformanceDto getProjectPerformance(...) { }
}
