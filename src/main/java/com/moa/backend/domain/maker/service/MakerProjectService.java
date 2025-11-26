package com.moa.backend.domain.maker.service;

import com.moa.backend.domain.maker.dto.manageproject.MakerProjectListItemResponse;
import com.moa.backend.domain.maker.dto.manageproject.MakerProjectListResponse;
import com.moa.backend.domain.maker.dto.manageproject.ProjectSummaryStatsResponse;
import com.moa.backend.domain.maker.entity.Maker;
import com.moa.backend.domain.maker.repository.MakerRepository;
import com.moa.backend.domain.order.entity.OrderStatus;
import com.moa.backend.domain.order.repository.OrderRepository;
import com.moa.backend.domain.project.entity.Project;
import com.moa.backend.domain.project.entity.ProjectLifecycleStatus;
import com.moa.backend.domain.project.entity.ProjectResultStatus;
import com.moa.backend.domain.project.entity.ProjectReviewStatus;
import com.moa.backend.domain.project.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 한글 설명:
 * - 메이커 마이페이지 > 내 프로젝트 목록/통계를 제공하는 서비스.
 * - 로그인 유저(userId)를 기반으로 Maker를 찾고, 그 Maker에 속한 Project들을 조회한다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MakerProjectService {

    private final MakerRepository makerRepository;
    private final ProjectRepository projectRepository;
    private final OrderRepository orderRepository;

    // ================== 내부용 상태/정렬 enum ==================

    /**
     * 한글 설명: 메이커 마이페이지 전용 상태 필터 값.
     * - HTTP 파라미터 status와 1:1로 매핑된다.
     */
    private enum MakerProjectStatusFilter {
        ALL,
        DRAFT,
        REVIEW,
        LIVE,
        ENDED_SUCCESS,
        ENDED_FAILED,
        REJECTED,
        SCHEDULED;

        static MakerProjectStatusFilter from(String raw) {
            if (raw == null || raw.isBlank()) {
                return ALL;
            }
            try {
                return MakerProjectStatusFilter.valueOf(raw.toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException ex) {
                throw new IllegalArgumentException("지원하지 않는 status 값입니다. status=" + raw);
            }
        }
    }

    /**
     * 한글 설명: 정렬 기준 파라미터(sortBy).
     */
    private enum SortKey {
        RECENT,      // 최신 수정순
        START_DATE,  // 시작일 순
        RAISED,      // 모금액 많은 순
        DEADLINE;    // 마감 임박순

        static SortKey from(String raw) {
            if (raw == null || raw.isBlank()) {
                return RECENT;
            }
            return switch (raw) {
                case "recent" -> RECENT;
                case "startDate" -> START_DATE;
                case "raised" -> RAISED;
                case "deadline" -> DEADLINE;
                default -> throw new IllegalArgumentException("지원하지 않는 sortBy 값입니다. sortBy=" + raw);
            };
        }
    }

    /**
     * 한글 설명: 내부 계산용 Row 객체.
     * - Project + 모금/서포터/진행률/남은일수/상태 문자열을 묶어서 다룬다.
     */
    private record MakerProjectRow(
            Project project,
            long fundedAmount,
            long supporterCount,
            double progressPercent,
            Integer daysLeft,
            String uiStatus
    ) {}

    // ================== 메인 기능 ==================

    /**
     * 한글 설명:
     * - 메이커의 프로젝트 목록을 상태/정렬/페이지네이션 기준으로 조회한다.
     *
     * @param userId      로그인 유저 ID (User.id, = Maker.owner.id)
     * @param statusParam DRAFT/REVIEW/LIVE/ENDED_SUCCESS/ENDED_FAILED/REJECTED/ALL
     * @param sortByParam recent/startDate/raised/deadline
     * @param page        1부터 시작
     * @param pageSize    페이지 크기
     */
    public MakerProjectListResponse getMakerProjects(
            Long userId,
            String statusParam,
            String sortByParam,
            int page,
            int pageSize
    ) {
        // 1) 유저 → Maker 조회
        Maker maker = makerRepository.findByOwner_Id(userId)
                .orElseThrow(() -> new NoSuchElementException("메이커를 찾을 수 없습니다. userId=" + userId));

        MakerProjectStatusFilter statusFilter = MakerProjectStatusFilter.from(statusParam);
        SortKey sortKey = SortKey.from(sortByParam);

        // 2) 이 메이커의 모든 프로젝트 조회
        List<Project> allProjects = projectRepository.findAllByMakerId(maker.getId());

        // 3) 각 프로젝트별 모금액/서포터/진행률/남은일수/상태 문자열 계산
        List<MakerProjectRow> rows = allProjects.stream()
                .map(this::toRowWithMetrics)
                .toList();

        // 4) 상태 필터 적용
        List<MakerProjectRow> filtered = rows.stream()
                .filter(row -> statusFilter == MakerProjectStatusFilter.ALL
                        || statusFilter.name().equals(row.uiStatus()))
                .toList();

        // 5) 정렬 기준 적용
        List<MakerProjectRow> sorted = sortRows(filtered, sortKey);

        // 6) 페이지네이션 계산
        long totalCount = sorted.size();
        int totalPages = (int) Math.ceil(totalCount / (double) pageSize);
        int safePage = Math.max(1, page);

        int fromIndex = (safePage - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, (int) totalCount);

        List<MakerProjectListItemResponse> pageContent;
        if (fromIndex >= totalCount) {
            pageContent = List.of();
        } else {
            pageContent = sorted.subList(fromIndex, toIndex).stream()
                    .map(this::toListItemResponse)
                    .collect(Collectors.toList());
        }

        return MakerProjectListResponse.builder()
                .projects(pageContent)
                .totalCount(totalCount)
                .page(safePage)
                .pageSize(pageSize)
                .totalPages(totalPages)
                .build();
    }

    /**
     * 한글 설명:
     * - 메이커 프로젝트 요약 통계 조회.
     * - 전체 개수, LIVE 개수, 총 모금액, 이번 달 신규 프로젝트 수를 계산한다.
     */
    public ProjectSummaryStatsResponse getProjectSummaryStats(Long userId) {
        // 1) 유저 → Maker 조회
        Maker maker = makerRepository.findByOwner_Id(userId)
                .orElseThrow(() -> new NoSuchElementException("메이커를 찾을 수 없습니다. userId=" + userId));

        List<Project> projects = projectRepository.findAllByMakerId(maker.getId());

        int totalProjects = projects.size();
        int liveProjects = (int) projects.stream()
                .filter(p -> p.getLifecycleStatus() == ProjectLifecycleStatus.LIVE
                        && p.getReviewStatus() == ProjectReviewStatus.APPROVED)
                .count();

        // 2) 총 모금액 합계 (PAID 기준)
        long totalRaised = projects.stream()
                .mapToLong(p -> {
                    Long funded = orderRepository.getTotalFundedAmount(p.getId());
                    return funded != null ? funded : 0L;
                })
                .sum();

        // 3) 이번 달 신규 프로젝트 수 (createdAt 기준)
        LocalDate now = LocalDate.now();
        int currentYear = now.getYear();
        int currentMonth = now.getMonthValue();

        int newProjectsThisMonth = (int) projects.stream()
                .filter(p -> {
                    LocalDateTime createdAt = p.getCreatedAt();
                    if (createdAt == null) return false;
                    LocalDate createdDate = createdAt.toLocalDate();
                    return createdDate.getYear() == currentYear
                            && createdDate.getMonthValue() == currentMonth;
                })
                .count();

        return ProjectSummaryStatsResponse.builder()
                .totalProjects(totalProjects)
                .liveProjects(liveProjects)
                .totalRaised(totalRaised)
                .newProjectsThisMonth(newProjectsThisMonth)
                .build();
    }

    // ================== 내부 유틸 메서드 ==================

    /**
     * 한글 설명:
     * - Project 엔티티 1개를 메이커 마이페이지용 Row 정보로 변환한다.
     * - 여기서 모금액/서포터/진행률/남은일수/UI 상태 문자열을 함께 계산한다.
     *
     * ⚠️ 주의: 프로젝트 개수가 많아지면 N+1 쿼리가 발생할 수 있다.
     *  - 나중에 성능 문제 생기면 group by 쿼리나 batch 조회로 최적화 필요.
     */
    private MakerProjectRow toRowWithMetrics(Project project) {
        Long funded = orderRepository.getTotalFundedAmount(project.getId());
        long fundedAmount = funded != null ? funded : 0L;

        Long supporter = orderRepository.countDistinctSupporterByProjectAndStatus(
                project.getId(),
                OrderStatus.PAID
        );
        long supporterCount = supporter != null ? supporter : 0L;

        double progressPercent = 0.0;
        if (project.getGoalAmount() != null && project.getGoalAmount() > 0) {
            progressPercent = (fundedAmount * 100.0) / project.getGoalAmount();
        }

        Integer daysLeft = null;
        if (project.getEndDate() != null) {
            LocalDate today = LocalDate.now();
            long diff = ChronoUnit.DAYS.between(today, project.getEndDate());
            if (diff >= 0) {
                daysLeft = (int) diff;
            }
        }

        String uiStatus = mapToUiStatus(project);

        return new MakerProjectRow(
                project,
                fundedAmount,
                supporterCount,
                progressPercent,
                daysLeft,
                uiStatus
        );
    }

    /**
     * 한글 설명:
     * - 메이커 마이페이지용 상태 문자열(DRAFT/REVIEW/LIVE/ENDED_SUCCESS/ENDED_FAILED/REJECTED)을
     *   Project의 lifecycleStatus/reviewStatus/resultStatus 조합으로부터 생성한다.
     */
    private String mapToUiStatus(Project project) {
        ProjectLifecycleStatus lifecycle = project.getLifecycleStatus();
        ProjectReviewStatus review = project.getReviewStatus();
        ProjectResultStatus result = project.getResultStatus();

        if (lifecycle == ProjectLifecycleStatus.DRAFT && review == ProjectReviewStatus.NONE) {
            return "DRAFT";
        }
        if (lifecycle == ProjectLifecycleStatus.DRAFT && review == ProjectReviewStatus.REVIEW) {
            return "REVIEW";
        }
        if (lifecycle == ProjectLifecycleStatus.LIVE && review == ProjectReviewStatus.APPROVED) {
            return "LIVE";
        }
        if (lifecycle == ProjectLifecycleStatus.ENDED && result == ProjectResultStatus.SUCCESS) {
            return "ENDED_SUCCESS";
        }
        if (lifecycle == ProjectLifecycleStatus.ENDED && result == ProjectResultStatus.FAILED) {
            return "ENDED_FAILED";
        }
        if (lifecycle == ProjectLifecycleStatus.DRAFT && review == ProjectReviewStatus.REJECTED) {
            return "REJECTED";
        }

        // 한글 설명: 예외적인 조합은 일단 lifecycle 기준으로 fallback.
        return lifecycle != null ? lifecycle.name() : "UNKNOWN";
    }

    /**
     * 한글 설명:
     * - 상태 필터 적용 후의 Row 목록에 대해 정렬 기준을 적용한다.
     */
    private List<MakerProjectRow> sortRows(List<MakerProjectRow> rows, SortKey sortKey) {
        Comparator<MakerProjectRow> comparator;

        switch (sortKey) {
            case START_DATE -> {
                comparator = Comparator.comparing(
                        (MakerProjectRow row) -> {
                            // startDate (LocalDate) 기준. null은 가장 뒤로.
                            LocalDate start = row.project().getStartDate();
                            return start != null ? start : LocalDate.MAX;
                        },
                        Comparator.naturalOrder()
                );
            }
            case RAISED -> {
                comparator = Comparator.comparingLong(MakerProjectRow::fundedAmount)
                        .reversed();
            }
            case DEADLINE -> {
                comparator = Comparator.comparing(
                        (MakerProjectRow row) -> {
                            LocalDate end = row.project().getEndDate();
                            return end != null ? end : LocalDate.MAX;
                        },
                        Comparator.naturalOrder()
                );
            }
            case RECENT -> {
                comparator = Comparator.comparing(
                        (MakerProjectRow row) -> {
                            LocalDateTime updated = row.project().getUpdatedAt();
                            LocalDateTime created = projectSafeCreatedAt(row.project());
                            return Objects.requireNonNullElseGet(
                                    updated,
                                    () -> created != null ? created : LocalDateTime.MIN
                            );
                        }
                ).reversed();
            }
            default -> {
                comparator = Comparator.comparing(
                        (MakerProjectRow row) -> row.project().getUpdatedAt(),
                        Comparator.nullsLast(LocalDateTime::compareTo)
                ).reversed();
            }
        }

        return rows.stream()
                .sorted(comparator)
                .toList();
    }

    private LocalDateTime projectSafeCreatedAt(Project p) {
        return p.getCreatedAt();
    }

    /**
     * 한글 설명:
     * - 내부 MakerProjectRow를 실제 응답 DTO로 변환한다.
     */
    private MakerProjectListItemResponse toListItemResponse(MakerProjectRow row) {
        Project project = row.project();

        return MakerProjectListItemResponse.builder()
                .id(project.getId())
                .title(project.getTitle())
                .category(project.getCategory())
                .status(row.uiStatus())
                .lifecycleStatus(project.getLifecycleStatus())
                .reviewStatus(project.getReviewStatus())
                .resultStatus(project.getResultStatus())
                .thumbnailUrl(project.getCoverImageUrl())
                .goalAmount(project.getGoalAmount())
                .currentAmount(row.fundedAmount())
                .progressPercent(row.progressPercent())
                .supporterCount(row.supporterCount())
                .daysLeft(row.daysLeft())
                .lastModifiedAt(project.getUpdatedAt())
                .build();
    }
}
