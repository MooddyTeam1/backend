package com.moa.backend.domain.maker.service;

import com.moa.backend.domain.maker.dto.manageproject.*;
import com.moa.backend.domain.maker.entity.ProjectNews;
import com.moa.backend.domain.maker.repository.ProjectNewsRepository;
import com.moa.backend.domain.order.entity.Order;
import com.moa.backend.domain.order.entity.OrderStatus;
import com.moa.backend.domain.order.repository.OrderRepository;
import com.moa.backend.domain.project.entity.Project;
import com.moa.backend.domain.project.entity.ProjectLifecycleStatus;
import com.moa.backend.domain.project.repository.ProjectRepository;
import com.moa.backend.domain.qna.entity.ProjectQna;
import com.moa.backend.domain.qna.repository.ProjectQnaRepository;
import com.moa.backend.domain.reward.entity.Reward;
import com.moa.backend.domain.reward.repository.RewardRepository;
import com.moa.backend.domain.tracking.entity.TrackingEvent;
import com.moa.backend.domain.tracking.entity.TrackingEventType;
import com.moa.backend.domain.tracking.repository.TrackingEventRepository;
import com.moa.backend.global.error.AppException;
import com.moa.backend.global.error.ErrorCode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 한글 설명: 메이커 프로젝트 상세 관리 서비스.
 *
 * - 명세서의 "/api/maker/projects/{projectId}" 응답을 만들어내는 핵심 비즈니스 로직을 담당한다.
 * - 컨트롤러에서는 이 서비스의 getMakerProjectDetail(...) 한 개만 호출하면 된다.
 *
 * ⚠ 주의:
 *  - 여기서는 필요한 리포지토리 메서드를 "가정"하고 호출한다.
 *  - 각 Repository에 @Query 등을 추가하면서 실제 구현을 채워 넣을 예정이다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MakerProjectManageService {

    // ===== 의존성 주입 리포지토리들 =====

    // 한글 설명: 프로젝트 기본 정보 + 메이커 소유권 검증 용
    private final ProjectRepository projectRepository;

    // 한글 설명: 트래킹 이벤트(뷰/채널) 집계 용
    private final TrackingEventRepository trackingEventRepository;

    // 한글 설명: 주문/모금/서포터/리워드 판매 통계 집계 용
    private final OrderRepository orderRepository;

    // 한글 설명: 리워드 목록/가격 정보 조회 용
    private final RewardRepository rewardRepository;

    // 한글 설명: 프로젝트 공지(소식) 조회 용
    private final ProjectNewsRepository projectNewsRepository;

    // 한글 설명: 프로젝트 Q&A 조회 용 (추후 구현)
     private final ProjectQnaRepository projectQnaRepository;

    // ===== 메인 진입점 =====

    /**
     * 한글 설명:
     * - 메이커 프로젝트 상세 관리 화면에서 사용할 모든 데이터를 한 번에 조회/조립한다.
     *
     * @param projectId   상세를 보고 싶은 프로젝트 ID
     * @param loginUserId 현재 로그인한 유저 ID (메이커 소유권 검증용)
     */
    public MakerProjectDetailResponse getMakerProjectDetail(Long projectId, Long loginUserId) {

        // 1) 프로젝트 + 메이커 소유권 검증 -------------------------------
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "프로젝트를 찾을 수 없습니다."));

        // 한글 설명: Maker 엔티티에서 owner(User) 기준으로 소유권 검증
        Long ownerUserId = project.getMaker().getOwner().getId();
        if (!Objects.equals(ownerUserId, loginUserId)) {
            throw new AppException(ErrorCode.FORBIDDEN, "해당 프로젝트에 대한 접근 권한이 없습니다.");
        }

        // 2) 기본 정보 계산 ---------------------------------------------
        Long goalAmount = Optional.ofNullable(project.getGoalAmount()).orElse(0L);

        // 한글 설명: currentAmount는 Order에서 PAID 상태 총합으로 계산
        Long currentAmount = calculateCurrentAmount(projectId);

        Double progressPercent = calculateProgressPercent(goalAmount, currentAmount);
        Integer supporterCount = calculateSupporterCount(projectId);

        // 한글 설명: 남은 일수는 '펀딩 종료일(endDate)' 기준
        Integer daysLeft = calculateDaysLeft(project.getLifecycleStatus(), project.getEndDate());

        String categoryLabel = resolveCategoryLabel(project);

        // 3) 통계/그래프/판매/정산 영역 데이터 계산 -----------------------

        // 3-1) 리워드별 판매 통계 (도넛 차트 + topReward 용)
        List<RewardSalesStatsResponse> rewardSalesStats = calculateRewardSalesStats(projectId);

        // 3-2) todayViews / totalViews / repeatSupporterRate / avg 등 상단 stats
        ProjectDetailStatsResponse stats = buildProjectDetailStats(
                projectId,
                goalAmount,
                currentAmount,
                supporterCount,
                rewardSalesStats
        );

        // 3-3) 일별 통계 (approvedAt ~ endDate, 최대 30일)
        List<DailyStatsResponse> dailyStats = buildDailyStats(project);

        // 3-4) 채널별 유입 통계
        List<ChannelStatsResponse> channelStats = buildChannelStats(projectId);

        // 3-5) 리워드 요약 목록
        List<RewardSummaryResponse> rewards = buildRewardSummaries(projectId);

        // 3-6) 최근 주문 목록 (최신 10개)
        List<MakerProjectOrderSummaryResponse> recentOrders = loadRecentOrders(projectId, 10);

        // 3-7) 공지(소식) 목록
        List<ProjectNoticeResponse> notices = loadProjectNotices(projectId);

        // 3-8) Q&A 목록 (아직 미구현 → 빈 리스트)
        List<ProjectQnaResponse> qnas = loadProjectQnas(projectId);

        // 3-9) 정산 정보 (간단 버전: 예상 정산액 계산)
        ProjectSettlementResponse settlement = calculateSettlement(projectId, currentAmount);

        // 4) 최종 DTO 조립 ---------------------------------------------

        return MakerProjectDetailResponse.builder()
                // 기본 정보
                .id(project.getId())
                .thumbnailUrl(project.getCoverImageUrl()) // 필요 시 썸네일 전용 필드로 변경
                .title(project.getTitle())
                .summary(project.getSummary())
                .category(categoryLabel)
                .status(project.getLifecycleStatus().name())
                .goalAmount(goalAmount)
                .currentAmount(currentAmount)
                .progressPercent(progressPercent)
                .supporterCount(supporterCount)
                .daysLeft(daysLeft)
                .daysLeft(daysLeft)
                // LocalDate -> LocalDateTime(00시) 로 변환
                .startDate(
                        project.getStartDate() != null
                                ? project.getStartDate().atStartOfDay()
                                : null
                )
                .endDate(
                        project.getEndDate() != null
                                ? project.getEndDate().atStartOfDay()
                                : null
                )

                // 통계/그래프
                .stats(stats)
                .dailyStats(dailyStats)
                .channelStats(channelStats)
                .rewardSalesStats(rewardSalesStats)

                // 리워드 / 주문 / 공지 / QnA
                .rewards(rewards)
                .recentOrders(recentOrders)
                .notices(notices)
                .qnas(qnas)

                // 정산
                .settlement(settlement)

                // 메타
                .createdAt(project.getCreatedAt())
                .updatedAt(project.getUpdatedAt())
                .approvedAt(project.getApprovedAt())
                .rejectedReason(project.getRejectedReason())
                .build();
    }

    // =====================================================================
    // 1) 기본 정보 계산 유틸
    // =====================================================================

    /**
     * 한글 설명: 현재 모금액(currentAmount)을 계산.
     * - Order 엔티티에서 해당 프로젝트 + PAID 상태 주문의 totalAmount 합산.
     */
    private Long calculateCurrentAmount(Long projectId) {
        // OrderRepository에 아래 메서드가 있다고 가정:
        // Optional<Long> sumTotalAmountByProjectIdAndStatus(Long projectId, OrderStatus status);
        return orderRepository
                .sumTotalAmountByProjectIdAndStatus(projectId, OrderStatus.PAID)
                .orElse(0L);
    }

    /**
     * 한글 설명: 진행률 계산 헬퍼 (0으로 나누기 방지).
     */
    private Double calculateProgressPercent(Long goalAmount, Long currentAmount) {
        if (goalAmount == null || goalAmount <= 0L) {
            return 0.0;
        }
        long raised = currentAmount != null ? currentAmount : 0L;
        double raw = raised / (double) goalAmount * 100.0;
        return Math.round(raw * 10.0) / 10.0; // 소수점 1자리
    }

    /**
     * 한글 설명: 종료일까지 남은 일수 계산.
     * - LIVE, SCHEDULED 상태에서만 남은 일수 계산
     * - 이미 종료된 경우 null
     *
     * @param lifecycleStatus 프로젝트 라이프사이클 상태
     * @param endDate         펀딩 종료일(LocalDate)
     */
    private Integer calculateDaysLeft(ProjectLifecycleStatus lifecycleStatus, LocalDate endDate) {
        if (endDate == null || lifecycleStatus == null) {
            return null;
        }

        if (!(lifecycleStatus == ProjectLifecycleStatus.LIVE
                || lifecycleStatus == ProjectLifecycleStatus.SCHEDULED)) {
            return null;
        }

        LocalDate today = LocalDate.now();
        long diff = ChronoUnit.DAYS.between(today, endDate);

        if (diff < 0) {
            return null;
        }
        return (int) diff;
    }

    /**
     * 한글 설명: 카테고리 한글 라벨 추출.
     * - Category enum에 displayName/koName 등의 필드가 없다면 name() 그대로 반환.
     */
    private String resolveCategoryLabel(Project project) {
        if (project.getCategory() == null) {
            return null;
        }
        // TODO: Category enum에 한글 라벨 필드가 있다면 해당 getter 사용 (ex: getKoName())
        return project.getCategory().name();
    }

    /**
     * 한글 설명: 서포터 수 (결제 완료 기준, 중복 제거).
     * - Order 엔티티 기준으로 distinct user / supporter 기준으로 계산.
     *
     * ▸ OrderRepository 예시:
     *   @Query("SELECT COUNT(DISTINCT o.user.id) FROM Order o WHERE o.project.id = :projectId AND o.status = :status")
     *   Integer countDistinctSupporterByProjectIdAndStatus(Long projectId, OrderStatus status);
     */
    private Integer calculateSupporterCount(Long projectId) {
        Integer count = orderRepository.countDistinctSupporterByProjectIdAndStatus(
                projectId,
                OrderStatus.PAID
        );
        return count != null ? count : 0;
    }

    // =====================================================================
    // 2) 상단 통계(stats) 계산
    // =====================================================================

    /**
     * 한글 설명:
     * - todayViews, totalViews, repeatSupporterRate, avgSupportAmount, topReward 등
     *   상단 stats 영역을 한 번에 조립한다.
     */
    private ProjectDetailStatsResponse buildProjectDetailStats(
            Long projectId,
            Long goalAmount,
            Long currentAmount,
            Integer supporterCount,
            List<RewardSalesStatsResponse> rewardSalesStats
    ) {
        // 1) 오늘 방문수 / 전체 방문수
        int todayViews = calculateTodayViews(projectId);
        long totalViews = calculateTotalViews(projectId);

        // 2) 재후원자 비율 계산
        double repeatSupporterRate = calculateRepeatSupporterRate(projectId, supporterCount);

        // 3) 평균 후원 금액 계산
        long totalRaised = currentAmount != null ? currentAmount : 0L;
        Long averageSupportAmount = 0L;
        if (supporterCount != null && supporterCount > 0) {
            averageSupportAmount = totalRaised / supporterCount;
        }

        // 4) 가장 많이 선택된 리워드 (rewardSalesStats 기반으로 선정)
        ProjectDetailStatsResponse.TopRewardResponse topReward = null;
        if (rewardSalesStats != null && !rewardSalesStats.isEmpty()) {
            RewardSalesStatsResponse top = rewardSalesStats.stream()
                    .max(Comparator.comparingInt(RewardSalesStatsResponse::getSalesCount))
                    .orElse(null);

            if (top != null) {
                topReward = ProjectDetailStatsResponse.TopRewardResponse.builder()
                        .id(top.getRewardId())
                        .title(top.getRewardTitle())
                        .count(top.getSalesCount())
                        .build();
            }
        }

        // 5) 달성률 다시 한 번 (stats에서도 사용)
        Double progressPercent = calculateProgressPercent(goalAmount, totalRaised);

        return ProjectDetailStatsResponse.builder()
                .todayViews(todayViews)
                .totalViews(totalViews)
                .totalRaised(totalRaised)
                .goalAmount(goalAmount)
                .progressPercent(progressPercent)
                .supporterCount(supporterCount)
                .repeatSupporterRate(repeatSupporterRate)
                .averageSupportAmount(averageSupportAmount)
                .topReward(topReward)
                .build();
    }

    /**
     * 한글 설명: 오늘 방문수 계산 (세션 기준).
     * - TrackingEvent에서 PROJECT_VIEW + 오늘 00:00~현재 기준 distinct sessionId 카운트.
     *
     * ▸ TrackingEventRepository 예시:
     *   Long countDistinctSessionIdByProjectIdAndEventTypeAndOccurredAtBetween(
     *       Long projectId, TrackingEventType type, LocalDateTime from, LocalDateTime to
     *   );
     */
    private int calculateTodayViews(Long projectId) {
        LocalDateTime from = LocalDate.now().atStartOfDay();
        LocalDateTime to = LocalDateTime.now();

        Long count = trackingEventRepository.countDistinctSessionIdByProjectIdAndEventTypeAndOccurredAtBetween(
                projectId,
                TrackingEventType.PROJECT_VIEW,
                from,
                to
        );
        return count != null ? count.intValue() : 0;
    }

    /**
     * 한글 설명: 전체 방문수 계산 (세션 기준).
     *
     * ▸ TrackingEventRepository 예시:
     *   Long countDistinctSessionIdByProjectIdAndEventType(Long projectId, TrackingEventType type);
     */
    private long calculateTotalViews(Long projectId) {
        Long count = trackingEventRepository.countDistinctSessionIdByProjectIdAndEventType(
                projectId,
                TrackingEventType.PROJECT_VIEW
        );
        return count != null ? count : 0L;
    }

    /**
     * 한글 설명:
     * - 재후원자 비율 계산.
     *   = (해당 프로젝트를 후원한 서포터 중, "이 플랫폼에서 2개 이상 프로젝트를 후원한 서포터" 비율.
     *
     * 구현 전략(단순 버전):
     *   1) 이 프로젝트의 결제완료 주문에서 distinct supporterId 목록 조회
     *   2) 각 supporterId에 대해, 결제완료 주문 기준 distinct projectId 개수를 세고, 2개 이상이면 "재후원자"로 간주
     *   3) (재후원자 수 / 전체 서포터 수) * 100
     *
     * ▸ OrderRepository 예시:
     *   List<Long> findDistinctSupporterIdsByProjectIdAndStatus(Long projectId, OrderStatus status);
     *   int countDistinctProjectIdBySupporterIdAndStatus(Long supporterId, OrderStatus status);
     */
    private double calculateRepeatSupporterRate(Long projectId, Integer supporterCount) {
        if (supporterCount == null || supporterCount <= 0) {
            return 0.0;
        }

        List<Long> supporterIds = orderRepository.findDistinctSupporterIdsByProjectIdAndStatus(
                projectId,
                OrderStatus.PAID
        );
        if (supporterIds == null || supporterIds.isEmpty()) {
            return 0.0;
        }

        long repeatSupporterCount = supporterIds.stream()
                .filter(supporterId -> {
                    int projectCount = orderRepository
                            .countDistinctProjectIdBySupporterIdAndStatus(supporterId, OrderStatus.PAID);
                    return projectCount > 1;
                })
                .count();

        double raw = repeatSupporterCount / (double) supporterCount * 100.0;
        return Math.round(raw * 10.0) / 10.0;
    }

    // =====================================================================
    // 3) 일별 통계 (dailyStats) 계산
    // =====================================================================

    /**
     * 한글 설명:
     * - 프로젝트 approvedAt ~ endDate(또는 오늘) 구간에서
     *   일별 방문수/신규 서포터 수/모금액을 계산한다.
     * - 최대 30일까지만 잘라서 보여준다.
     */
    private List<DailyStatsResponse> buildDailyStats(Project project) {
        LocalDate today = LocalDate.now();

        // 한글 설명: 시작일 = approvedAt 날짜, 없으면 오늘-29일
        LocalDate startDate = (project.getApprovedAt() != null)
                ? project.getApprovedAt().toLocalDate()
                : today.minusDays(29);

        // 한글 설명: 종료일 = 프로젝트 endDate (없으면 오늘)
        LocalDate endDate = (project.getEndDate() != null)
                ? project.getEndDate()
                : today;

        // 미래 날짜 방지
        if (endDate.isAfter(today)) {
            endDate = today;
        }

        // 시작일이 종료일보다 뒤인 경우 방어
        if (startDate.isAfter(endDate)) {
            startDate = endDate;
        }

        // 최대 30일로 제한
        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);
        if (daysBetween > 29) {
            startDate = endDate.minusDays(29);
        }

        List<DailyStatsResponse> result = new ArrayList<>();

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            LocalDateTime from = date.atStartOfDay();
            LocalDateTime to = date.plusDays(1).atStartOfDay();

            // 1) 방문수 (PROJECT_VIEW 기준, distinct sessionId)
            Long viewCount = trackingEventRepository
                    .countDistinctSessionIdByProjectIdAndEventTypeAndOccurredAtBetween(
                            project.getId(),
                            TrackingEventType.PROJECT_VIEW,
                            from,
                            to
                    );

            // 2) 신규 서포터 수 (해당 날짜에 이 프로젝트에 "첫 결제" 한 서포터 수)
            Integer newSupporters = orderRepository.countNewSupportersForProjectOnDate(
                    project.getId(),
                    date
            );

            // 3) 모금액 합계 (해당 날짜에 결제완료(PAID)된 주문 totalAmount 합)
            Long amount = orderRepository.sumPaidAmountForProjectOnDate(
                    project.getId(),
                    date
            );

            result.add(
                    DailyStatsResponse.builder()
                            .date(date)
                            .views(viewCount != null ? viewCount.intValue() : 0)
                            .supporters(newSupporters != null ? newSupporters : 0)
                            .amount(amount != null ? amount : 0L)
                            .build()
            );
        }

        return result;
    }

    // =====================================================================
    // 4) 채널별 유입 통계 (channelStats)
    // =====================================================================

    /**
     * 한글 설명:
     * - 프로젝트 상세 페이지 방문(TrackingEventType.PROJECT_VIEW)을 기준으로
     *   referrer / supporter.acquisitionChannel 등을 이용해 채널별 유입 통계를 계산한다.
     */
    private List<ChannelStatsResponse> buildChannelStats(Long projectId) {
        List<TrackingEvent> events = trackingEventRepository.findByProject_IdAndEventType(
                projectId,
                TrackingEventType.PROJECT_VIEW
        );

        if (events.isEmpty()) {
            return List.of();
        }

        Map<String, Long> channelCountMap = new HashMap<>();

        for (TrackingEvent event : events) {
            String channel = resolveChannel(event); // 아래 helper 참고
            channelCountMap.merge(channel, 1L, Long::sum);
        }

        long totalCount = channelCountMap.values().stream()
                .mapToLong(Long::longValue)
                .sum();

        if (totalCount == 0) {
            return List.of();
        }

        return channelCountMap.entrySet().stream()
                .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
                .limit(5)
                .map(entry -> {
                    String channel = entry.getKey();
                    long count = entry.getValue();

                    double percentage = (count / (double) totalCount) * 100.0;
                    double rounded = Math.round(percentage * 10.0) / 10.0;

                    return ChannelStatsResponse.builder()
                            .channel(channel)
                            .count((int) count)
                            .percentage(rounded)
                            .build();
                })
                .collect(Collectors.toList());
    }

    /**
     * 한글 설명:
     * - TrackingEvent에서 supporter의 acquisitionChannel 우선 사용,
     *   없으면 referrer URL을 기반으로 채널 문자열로 변환.
     */
    private String resolveChannel(TrackingEvent event) {
        // 1) 온보딩에서 선택한 유입 채널이 있으면 우선 사용
        if (event.getSupporter() != null && event.getSupporter().getAcquisitionChannel() != null) {
            return event.getSupporter().getAcquisitionChannel().name();
        }

        // 2) referrer 기반 분류
        String referrer = event.getReferrer();
        if (referrer == null || referrer.isBlank()) {
            return "직접 방문";
        }

        String lower = referrer.toLowerCase(Locale.ROOT);

        if (lower.contains("google.") ||
                lower.contains("naver.") ||
                lower.contains("daum.") ||
                lower.contains("bing.")) {
            return "검색";
        }

        if (lower.contains("instagram.com")) {
            return "인스타그램";
        }

        if (lower.contains("tistory.com") ||
                lower.contains("blog.naver.com") ||
                lower.contains("brunch.co.kr")) {
            return "블로그";
        }

        if (lower.contains("talk.kakao.com") ||
                lower.contains("pf.kakao.com") ||
                lower.contains("kakao.com")) {
            return "카카오톡";
        }

        // TODO: 본인 서비스 도메인(ex: moa.com) 포함 시 "직접 방문" 처리 가능
        return "기타";
    }

    // =====================================================================
    // 5) 리워드별 판매 통계 (rewardSalesStats)
    // =====================================================================

    /**
     * 한글 설명:
     * - 결제 완료(PAID) 주문 기준으로 리워드별 판매 수량/금액/비율을 집계한다.
     *
     * ▸ OrderRepository 예시:
     *   List<Object[]> findRewardSalesStatsByProjectId(Long projectId);
     *
     *   // Object[] 구조:
     *   // [0] Long rewardId
     *   // [1] String rewardTitle
     *   // [2] Long salesCount
     *   // [3] Long totalAmount
     */
    private List<RewardSalesStatsResponse> calculateRewardSalesStats(Long projectId) {
        List<Object[]> rows = orderRepository.findRewardSalesStatsByProjectId(projectId);
        if (rows == null || rows.isEmpty()) {
            return List.of();
        }

        // 한글 설명: 총 판매 수량 합계 (비율 계산용)
        long totalSalesCount = rows.stream()
                .mapToLong(row -> {
                    Long count = (Long) row[2];
                    return count != null ? count : 0L;
                })
                .sum();

        if (totalSalesCount <= 0L) {
            // 판매가 0이면 비율 전체 0으로 리턴
            return rows.stream()
                    .map(row -> RewardSalesStatsResponse.builder()
                            .rewardId((Long) row[0])
                            .rewardTitle((String) row[1])
                            .salesCount(((Long) row[2]).intValue())
                            .totalAmount((Long) row[3])
                            .percentage(0.0)
                            .build()
                    )
                    .collect(Collectors.toList());
        }

        return rows.stream()
                .map(row -> {
                    Long rewardId = (Long) row[0];
                    String title = (String) row[1];
                    Long salesCount = (Long) row[2];
                    Long totalAmount = (Long) row[3];

                    long count = salesCount != null ? salesCount : 0L;
                    long amount = totalAmount != null ? totalAmount : 0L;

                    double percentage = (count / (double) totalSalesCount) * 100.0;
                    double rounded = Math.round(percentage * 10.0) / 10.0;

                    return RewardSalesStatsResponse.builder()
                            .rewardId(rewardId)
                            .rewardTitle(title)
                            .salesCount((int) count)
                            .totalAmount(amount)
                            .percentage(rounded)
                            .build();
                })
                .collect(Collectors.toList());
    }

    // =====================================================================
    // 6) 리워드 목록 (rewards)
    // =====================================================================

    /**
     * 한글 설명:
     * - 프로젝트에 등록된 리워드 목록을 요약 형태로 반환한다.
     * - 판매 수량은 Order에서 리워드별 판매 수량 합계를 함께 조회해 사용한다.
     *
     * ▸ RewardRepository 예시:
     *   List<Reward> findByProject_Id(Long projectId);
     *
     * ▸ OrderRepository 예시 (기본 Map 변환 메서드는 default로 구현):
     *   List<Object[]> findRewardSalesCountByProjectId(Long projectId);
     */
    private List<RewardSummaryResponse> buildRewardSummaries(Long projectId) {
        List<Reward> rewards = rewardRepository.findByProject_Id(projectId);
        if (rewards == null || rewards.isEmpty()) {
            return List.of();
        }

        // 한글 설명: 리워드별 판매 수량 맵 (선택 사항)
        Map<Long, Long> salesCountMap = orderRepository
                .findRewardSalesCountMapByProjectId(projectId); // 리포지토리 default 메서드로 구현했다고 가정

        return rewards.stream()
                .map(reward -> {
                    Long salesCount = (salesCountMap != null)
                            ? salesCountMap.getOrDefault(reward.getId(), 0L)
                            : 0L;

                    return RewardSummaryResponse.builder()
                            .id(reward.getId())
                            .title(reward.getName())              // Reward.name 사용
                            .price(reward.getPrice())
                            .salesCount(salesCount.intValue())
                            .limitQty(reward.getStockQuantity())
                            .available(reward.isActive())         // active 필드 사용
                            .build();
                })
                .collect(Collectors.toList());
    }

    // =====================================================================
    // 7) 최근 주문 목록 (recentOrders)
    // =====================================================================

    /**
     * 한글 설명:
     * - 해당 프로젝트의 주문을 최신순으로 일부만 조회한다.
     *
     * ▸ OrderRepository 예시:
     *   @Query("SELECT o FROM Order o WHERE o.project.id = :projectId ORDER BY o.createdAt DESC")
     *   List<Order> findRecentOrdersForProject(Long projectId, Pageable pageable);
     */
    private List<MakerProjectOrderSummaryResponse> loadRecentOrders(Long projectId, int limit) {
        // 🔧 PageRequest.of(...) 쓰지 말고, limit int 그대로 전달
        List<Order> orders = orderRepository.findRecentOrdersForProject(projectId, limit);
        if (orders == null || orders.isEmpty()) {
            return List.of();
        }

        return orders.stream()
                .map(order -> {
                    // 한글 설명: 대표 리워드는 주문의 첫 번째 OrderItem 기준으로 간단히 잡는다.
                    String rewardTitle = null;
                    Long rewardId = null;
                    if (order.getOrderItems() != null && !order.getOrderItems().isEmpty()) {
                        var firstItem = order.getOrderItems().get(0);
                        if (firstItem.getReward() != null) {
                            rewardTitle = firstItem.getReward().getName();
                            rewardId = firstItem.getReward().getId();
                        } else {
                            // 스냅샷만 있는 경우
                            rewardTitle = firstItem.getRewardName();
                        }
                    }

                    return MakerProjectOrderSummaryResponse.builder()
                            .orderId(order.getId())
                            .orderCode(order.getOrderCode())
                            .supporterName(order.getUser().getName()) // TODO: 닉네임 기준으로 바꾸고 싶으면 수정
                            .supporterId(order.getUser().getId())
                            .rewardTitle(rewardTitle)
                            .rewardId(rewardId)
                            .amount(order.getTotalAmount())
                            .paymentStatus(order.getStatus().name())         // OrderStatus 사용
                            .deliveryStatus(order.getDeliveryStatus().name()) // DeliveryStatus 사용
                            .orderedAt(order.getCreatedAt())
                            .paidAt(null) // 별도 paidAt 필드가 없으니 필요 시 createdAt/추가 필드로 대체
                            .build();
                })
                .collect(Collectors.toList());
    }

    // =====================================================================
    // 8) 공지 / Q&A
    // =====================================================================

    /**
     * 한글 설명:
     * - 프로젝트에 등록된 공지/새소식을 조회한다.
     * - ProjectNewsRepository에서 pinned DESC, createdAt DESC 순으로 가져온 뒤
     *   ProjectNoticeResponse로 변환한다.
     */
    private List<ProjectNoticeResponse> loadProjectNotices(Long projectId) {
        List<ProjectNews> notices = projectNewsRepository
                .findByProject_IdOrderByPinnedDescCreatedAtDesc(projectId);

        if (notices == null || notices.isEmpty()) {
            return List.of();
        }

        return notices.stream()
                .map(ProjectNoticeResponse::from)
                .toList();
    }

    /**
     * 한글 설명:
     * - 메이커 프로젝트 상세 콘솔 상단/요약에 보여줄 Q&A 목록을 간단히 조회한다.
     * - 상세 필터/페이징은 별도 Q&A API(ProjectQnaService)에서 처리.
     */
    private List<ProjectQnaResponse> loadProjectQnas(Long projectId) {
        List<ProjectQna> qnas = projectQnaRepository
                .findByProject_IdOrderByCreatedAtDesc(projectId);

        if (qnas == null || qnas.isEmpty()) {
            return List.of();
        }

        return qnas.stream()
                .map(qna -> ProjectQnaResponse.builder()
                        .id(qna.getId())
                        .questionerName(
                                qna.getQuestioner() != null
                                        ? qna.getQuestioner().getName()   // 한글 설명: 콘솔 요약에서는 일단 User.name 사용
                                        : null
                        )
                        .questionerId(
                                qna.getQuestioner() != null
                                        ? qna.getQuestioner().getId()
                                        : null
                        )
                        .question(qna.getQuestion())
                        .answer(qna.getAnswer())
                        .status(qna.getStatus() != null ? qna.getStatus().name() : null)
                        .createdAt(qna.getCreatedAt())
                        .answeredAt(qna.getAnsweredAt())
                        .build()
                )
                .toList();
    }

    // =====================================================================
    // 9) 정산 정보 (settlement)
    // =====================================================================

    /**
     * 한글 설명:
     * - 간단한 "예상 정산 정보"를 계산한다.
     * - 명세 기준:
     *   platformFee = totalRaised * 0.05
     *   pgFee       = totalRaised * 0.03
     *   otherFees   = 0 (우선)
     *   finalAmount = totalRaised - (platformFee + pgFee + otherFees)
     *
     * - 실제 정산 계좌 정보는 "메이커 정산 계좌 관리 API"에서 가져와야 한다.
     *   여기서는 placeholder로 null을 넣고, 이후 settlement 계좌 엔티티와 연결 예정.
     */
    private ProjectSettlementResponse calculateSettlement(Long projectId, Long totalRaised) {
        long raised = totalRaised != null ? totalRaised : 0L;

        long platformFee = Math.round(raised * 0.05);
        long pgFee = Math.round(raised * 0.03);
        long otherFees = 0L;
        long finalAmount = raised - (platformFee + pgFee + otherFees);

        if (finalAmount < 0) {
            finalAmount = 0;
        }

        // TODO: 실제 정산 확정일 / 예정일 / 계좌 정보는
        //  메이커 정산 계좌 엔티티, 프로젝트 정산 엔티티 등과 연동해서 채워 넣기.
        return ProjectSettlementResponse.builder()
                .totalRaised(raised)
                .platformFee(platformFee)
                .pgFee(pgFee)
                .otherFees(otherFees)
                .finalAmount(finalAmount)
                .paymentConfirmedAt(null)
                .settlementScheduledAt(null)
                .bankName(null)
                .accountNumber(null)
                .accountHolder(null)
                .build();
    }
}
