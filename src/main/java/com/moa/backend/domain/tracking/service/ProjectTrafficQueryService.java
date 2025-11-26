package com.moa.backend.domain.tracking.service;

import com.moa.backend.domain.order.entity.OrderStatus;
import com.moa.backend.domain.order.repository.OrderRepository;
import com.moa.backend.domain.project.dto.ProjectListResponse; // ✅ 카드 공통 DTO
import com.moa.backend.domain.project.dto.TrendingProjectResponse;
import com.moa.backend.domain.project.entity.Project;
import com.moa.backend.domain.project.entity.ProjectLifecycleStatus;
import com.moa.backend.domain.project.entity.ProjectReviewStatus;
import com.moa.backend.domain.project.repository.ProjectRepository;
import com.moa.backend.domain.tracking.entity.TrackingEvent;
import com.moa.backend.domain.tracking.entity.TrackingEventType;
import com.moa.backend.domain.tracking.repository.TrackingEventRepository;
import com.moa.backend.domain.user.entity.SupporterProfile;
import com.moa.backend.domain.user.repository.SupporterProfileRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 트래킹 이벤트를 기록하고,
 * 해당 데이터를 기반으로 "지금 많이 보고 있는 프로젝트" / "지금 뜨는 프로젝트" 리스트를 계산하는 서비스.
 *
 * 기능 개요
 *  1) 기록(Tracking)
 *     - 프로젝트 상세 진입 시 trackProjectView(...) 호출
 *     - 이후 카드 노출/클릭, 후원 버튼 클릭 등 다른 이벤트도 같은 패턴으로 확장 가능
 *
 *  2) 조회(Query)
 *     - getMostViewedProjects(...): 최근 N분/시간 동안 조회수가 많은 프로젝트 상위 N개
 *     - getTrendingProjectsWithScore(...): 조회수 + 찜 수 + 결제 금액을 조합한 점수 기반 트렌딩 프로젝트
 *
 *  ⚙️ 응답 형태
 *     - 모든 리스트는 공통 카드 DTO인 ProjectListResponse 를 사용한다.
 *     - 트래킹 전용 필드(recentViewCount, trafficWindowLabel, trendingScore 등)는
 *       ProjectListResponse 내부에 통합되어 있다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectTrafficQueryService {

    private final TrackingEventRepository trackingEventRepository;
    private final ProjectRepository projectRepository;
    private final OrderRepository orderRepository;
    private final SupporterProfileRepository supporterProfileRepository;

    // ==========================
    // 1. 이벤트 기록 메서드
    // ==========================

    /**
     * 프로젝트 상세 페이지 진입 시 "프로젝트 뷰" 이벤트를 기록한다.
     *
     * @param projectId   프로젝트 ID
     * @param supporterId 서포터 프로필 ID (비로그인/서포터 아님인 경우 null 허용)
     * @param sessionId   세션 식별자 (쿠키/로컬스토리지 등으로 관리)
     * @param request     HttpServletRequest (IP, UA, referrer 등 추출용)
     */
    @Transactional
    public void trackProjectView(Long projectId,
                                 Long supporterId,
                                 String sessionId,
                                 HttpServletRequest request) {

        // 프로젝트 존재 여부 검증
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("프로젝트를 찾을 수 없습니다. id=" + projectId));

        // supporterId가 있으면 서포터 엔티티 조회 (없으면 null)
        SupporterProfile supporter = null;
        if (supporterId != null) {
            supporter = supporterProfileRepository.findById(supporterId).orElse(null);
        }

        TrackingEvent event = TrackingEvent.builder()
                .supporter(supporter)                        // 한글 설명: user 대신 supporter 기준으로 트래킹
                .project(project)
                .sessionId(sessionId)
                .eventType(TrackingEventType.PROJECT_VIEW)
                .path(request.getRequestURI())
                .referrer(request.getHeader("Referer"))
                .userAgent(request.getHeader("User-Agent"))
                .clientIp(extractClientIp(request))
                .extraJson(null) // A/B 테스트 버전, 실험군 정보 등 필요 시 JSON 문자열로 저장
                .build();

        trackingEventRepository.save(event);
    }

    /**
     * Proxy / 로드밸런서 환경을 고려한 클라이언트 IP 추출 헬퍼.
     */
    private String extractClientIp(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            // "client, proxy1, proxy2" 형태일 수 있으므로 첫 번째 값만 사용
            return xff.split(",")[0].trim();
        }
        return Optional.ofNullable(request.getRemoteAddr()).orElse("UNKNOWN");
    }

    // ==========================
    // 2. 지금 많이 보고 있는 프로젝트
    // ==========================

    /**
     * "지금 많이 보고 있는 프로젝트" 섹션용 데이터 조회.
     *
     * 예시:
     *  - windowDuration: Duration.ofHours(1)  → 최근 1시간
     *  - size: 10                             → 상위 10개
     *
     * @param windowDuration 조회 기간
     * @param size           최대 개수
     * @param windowLabel    FE에서 쓸 라벨 (예: "최근 1시간")
     */
    public List<ProjectListResponse> getMostViewedProjects(Duration windowDuration,
                                                           int size,
                                                           String windowLabel) {
        int safeSize = Math.max(1, Math.min(size, 30));

        LocalDateTime to = LocalDateTime.now();
        LocalDateTime from = to.minus(windowDuration);

        // 1) 최근 기간 동안 PROJECT_VIEW 이벤트 수가 많은 프로젝트 상위 safeSize개 조회
        List<Object[]> rows = trackingEventRepository.findTopProjectsByEventTypeAndPeriod(
                TrackingEventType.PROJECT_VIEW,
                from,
                to,
                PageRequest.of(0, safeSize)
        );

        log.info("[MOST_VIEWED] rows size={}, from={}, to={}", rows.size(), from, to);
        rows.forEach(r ->
                log.info("[MOST_VIEWED ROW] projectId={}, viewCount={}", r[0], r[1])
        );

        if (rows.isEmpty()) {
            return List.of();
        }

        // 2) 조회 결과에서 projectId 목록 추출
        List<Long> projectIds = rows.stream()
                .map(r -> (Long) r[0])
                .collect(Collectors.toList());

        // 3) 프로젝트 엔티티 한 번에 조회 (N+1 문제 방지)
        Map<Long, Project> projectMap = projectRepository.findAllById(projectIds).stream()
                .collect(Collectors.toMap(Project::getId, p -> p));

        // 4) 결과 순서를 유지하면서 ProjectListResponse로 변환
        List<ProjectListResponse> result = new ArrayList<>();
        for (Object[] row : rows) {
            Long projectId = (Long) row[0];
            Long viewCount = (Long) row[1];

            Project project = projectMap.get(projectId);
            if (project == null) {
                // 이미 삭제된 프로젝트일 수 있으므로 스킵
                continue;
            }

            long safeViewCount = (viewCount != null) ? viewCount : 0L;

            // ✅ 한글 설명: 공통 카드 + 트래픽 필드를 담은 ProjectListResponse로 변환
            result.add(
                    ProjectListResponse.fromMostViewed(
                            project,
                            safeViewCount,
                            windowLabel
                    )
            );
        }

        return result;
    }

    // ==========================
    // 3. 지금 뜨는 프로젝트 (점수 기반)
    // ==========================

    /**
     * "지금 뜨는 프로젝트" 점수 기반 트렌딩 리스트 조회.
     *
     * 대상:
     *  - 라이프사이클: LIVE, SCHEDULED
     *  - 심사 상태: APPROVED
     *
     * 지표:
     *  - recentViewCount : 최근 24시간 PROJECT_VIEW 수
     *  - bookmarkCount   : 프로젝트 찜 수 (TrendingProjectResponse 기반)
     *  - paidAmount      : 결제 상태가 PAID인 주문 총 결제 금액
     *
     * 점수 계산:
     *  - 각 지표를 0~1로 정규화한 뒤 가중합
     *  - 모든 지표가 0인 경우에도 NaN 이 발생하지 않도록 0으로 처리
     *
     * 응답:
     *  - 공통 카드 DTO(ProjectListResponse)에
     *    recentViewCount / bookmarkCount / fundedAmount / trendingScore / achievementRate
     *    정보를 함께 내려준다.
     */
    public List<ProjectListResponse> getTrendingProjectsWithScore(int size) {
        int safeSize = Math.max(1, Math.min(size, 30));

        // 1) 북마크 상위 프로젝트를 1차 후보로 가져오기
        List<ProjectLifecycleStatus> statuses = List.of(
                ProjectLifecycleStatus.LIVE,
                ProjectLifecycleStatus.SCHEDULED
        );

        List<TrendingProjectResponse> bookmarkBased = projectRepository.findTrendingProjects(
                statuses,
                ProjectReviewStatus.APPROVED,
                PageRequest.of(0, safeSize * 3) // 넉넉하게 가져온 뒤 점수 기준으로 재정렬
        );

        if (bookmarkBased.isEmpty()) {
            log.info("[TRENDING] 북마크 기반 후보가 없습니다.");
            return List.of();
        }
        log.info("[TRENDING] 북마크 기반 후보 개수={}", bookmarkBased.size());

        LocalDateTime viewSince = LocalDateTime.now().minusHours(24);

        // 2) 프로젝트별 원시 지표 계산
        List<ProjectScoreRaw> rawList = new ArrayList<>();

        for (TrendingProjectResponse item : bookmarkBased) {
            Long projectId = item.getId();

            // 2-1) 최근 24시간 조회수
            long recentViewCount = Optional.ofNullable(
                    trackingEventRepository.countByProject_IdAndEventTypeAndOccurredAtAfter(
                            projectId,
                            TrackingEventType.PROJECT_VIEW,
                            viewSince
                    )
            ).orElse(0L);

            // 2-2) 찜 개수 (프로젝션 DTO에 포함된 값 사용)
            long bookmarkCount = item.getBookmarkCount();

            // 2-3) 결제 완료 금액 (PAID 상태 기준)
            long paidAmount = orderRepository
                    .sumTotalAmountByProjectIdAndStatus(projectId, OrderStatus.PAID)
                    .orElse(0L);

            log.info("[TRENDING RAW] projectId={}, recentViewCount={}, bookmarkCount={}, paidAmount={}",
                    projectId, recentViewCount, bookmarkCount, paidAmount);

            rawList.add(
                    new ProjectScoreRaw(
                            item,
                            recentViewCount,
                            bookmarkCount,
                            paidAmount
                    )
            );
        }

        if (rawList.isEmpty()) {
            // 후보는 있으나 실제 지표 계산 결과가 비어 있는 경우 방어 코드
            return List.of();
        }

        // 3) 지표별 최대값 계산 (0일 수 있다는 전제 하에 처리 → NaN 방지)
        long maxView = rawList.stream().mapToLong(ProjectScoreRaw::recentViewCount).max().orElse(0L);
        long maxBookmark = rawList.stream().mapToLong(ProjectScoreRaw::bookmarkCount).max().orElse(0L);
        long maxPaid = rawList.stream().mapToLong(ProjectScoreRaw::paidAmount).max().orElse(0L);

        log.info("[TRENDING MAX] maxView={}, maxBookmark={}, maxPaid={}",
                maxView, maxBookmark, maxPaid);

        // 지표별 가중치 (운영하면서 조정 가능)
        double W_VIEW = 0.3;      // 조회수 비중
        double W_BOOKMARK = 0.4;  // 찜 비중
        double W_PAID = 0.3;      // 결제 금액 비중

        // 4) 프로젝트별 최종 점수 계산 (max가 0이면 해당 지표 점수는 0으로 처리)
        for (ProjectScoreRaw raw : rawList) {
            double viewScore =
                    (maxView > 0) ? (double) raw.recentViewCount() / maxView : 0.0;
            double bookmarkScore =
                    (maxBookmark > 0) ? (double) raw.bookmarkCount() / maxBookmark : 0.0;
            double paidScore =
                    (maxPaid > 0) ? (double) raw.paidAmount() / maxPaid : 0.0;

            double totalScore = W_VIEW * viewScore
                    + W_BOOKMARK * bookmarkScore
                    + W_PAID * paidScore;

            // 모든 지표가 0일 때도 확실히 0으로 맞춤 (NaN / Infinity 방지)
            if (Double.isNaN(totalScore) || Double.isInfinite(totalScore)) {
                totalScore = 0.0;
            }

            raw.setScore(totalScore);

            log.info("[TRENDING SCORE] projectId={}, viewScore={}, bookmarkScore={}, paidScore={}, totalScore={}",
                    raw.item().getId(), viewScore, bookmarkScore, paidScore, totalScore);
        }

        // 5) 점수 계산이 끝난 프로젝트들에 대해 실제 Project 엔티티를 한 번에 조회
        Set<Long> projectIds = rawList.stream()
                .map(r -> r.item().getId())
                .collect(Collectors.toSet());

        Map<Long, Project> projectMap = projectRepository.findAllById(projectIds).stream()
                .collect(Collectors.toMap(Project::getId, p -> p));

        // 6) 점수 기준 내림차순 정렬 후 상위 safeSize개만 ProjectListResponse로 변환
        return rawList.stream()
                .sorted(Comparator.comparing(ProjectScoreRaw::score).reversed())
                .limit(safeSize)
                .map(raw -> {
                    TrendingProjectResponse item = raw.item();
                    Project project = projectMap.get(item.getId());
                    if (project == null) {
                        // 삭제되었거나 조회에 실패한 프로젝트는 스킵
                        return null;
                    }

                    // ✅ 한글 설명: 달성률(%) 계산: paidAmount / goalAmount * 100
                    Integer achievementRate = null;
                    Long goal = project.getGoalAmount();
                    if (goal != null && goal > 0) {
                        double rate = (double) raw.paidAmount() / goal;
                        achievementRate = (int) Math.floor(rate * 100);
                    }

                    // ✅ 공통 카드 + 트래킹/결제 지표를 함께 세팅한 ProjectListResponse 생성
                    return ProjectListResponse.fromTrending(
                            project,
                            raw.recentViewCount(),
                            raw.bookmarkCount(),
                            raw.paidAmount(),
                            raw.score(),
                            achievementRate
                    );
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * 점수 계산을 위한 내부용 DTO 성격의 클래스.
     * 서비스 외부로 노출되지 않으므로 private static class 로 유지.
     */
    private static class ProjectScoreRaw {
        private final TrendingProjectResponse item;
        private final long recentViewCount;
        private final long bookmarkCount;
        private final long paidAmount;
        private double score;

        public ProjectScoreRaw(TrendingProjectResponse item,
                               long recentViewCount,
                               long bookmarkCount,
                               long paidAmount) {
            this.item = item;
            this.recentViewCount = recentViewCount;
            this.bookmarkCount = bookmarkCount;
            this.paidAmount = paidAmount;
        }

        public TrendingProjectResponse item() {
            return item;
        }

        public long recentViewCount() {
            return recentViewCount;
        }

        public long bookmarkCount() {
            return bookmarkCount;
        }

        public long paidAmount() {
            return paidAmount;
        }

        public double score() {
            return score;
        }

        public void setScore(double score) {
            this.score = score;
        }
    }
}
