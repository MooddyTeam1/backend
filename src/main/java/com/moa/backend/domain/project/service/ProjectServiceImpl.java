package com.moa.backend.domain.project.service;

import com.moa.backend.domain.follow.service.SupporterProjectBookmarkService;
import com.moa.backend.domain.maker.entity.Maker;
import com.moa.backend.domain.maker.repository.MakerRepository;
import com.moa.backend.domain.order.entity.OrderStatus;
import com.moa.backend.domain.order.repository.OrderRepository;
import com.moa.backend.domain.project.dto.ProjectDetailResponse;
import com.moa.backend.domain.project.dto.ProjectListResponse;
import com.moa.backend.domain.project.dto.StatusSummaryResponse;
import com.moa.backend.domain.project.dto.TempProject.TempProjectResponse;
import com.moa.backend.domain.project.entity.Category;
import com.moa.backend.domain.project.entity.Project;
import com.moa.backend.domain.project.entity.ProjectLifecycleStatus;
import com.moa.backend.domain.project.entity.ProjectReviewStatus;
import com.moa.backend.domain.project.repository.ProjectRepository;
import com.moa.backend.global.error.AppException;
import com.moa.backend.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.moa.backend.domain.project.entity.ProjectResultStatus;
import org.springframework.data.domain.PageRequest;

import java.awt.image.PixelGrabber;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import com.moa.backend.domain.project.dto.TrendingProjectResponse;
import org.springframework.data.domain.PageRequest;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final MakerRepository makerRepository;
    private final SupporterProjectBookmarkService supporterProjectBookmarkService;

    // 결제 완료(PAID) 주문 기준으로 펀딩 금액을 합산하기 위해 OrderRepository 사용.
    private final OrderRepository orderRepository;

    // 프로젝트 + 모금액 + 달성률(0.0 ~ n.n)을 담는 내부용 레코드.
    private record ProjectProgress(Project project, long fundedAmount, double progressRate) { }


    //프로젝트 전체조회
    @Override
    public List<ProjectDetailResponse> getAll() {
        return projectRepository.findAll().stream()
                .map(ProjectDetailResponse::from)
                .collect(Collectors.toList());
    }

    //프로젝트 단일 조회
    @Override
    public ProjectDetailResponse getById(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("프로젝트을 찾을 수없습니다. id=" + projectId));

        return ProjectDetailResponse.from(project);
    }

    //제목 검색
    @Override
    public List<ProjectListResponse> searchByTitle(String keyword) {
        return projectRepository.searchByTitle(keyword).stream()
                .map(ProjectListResponse::searchProjects)
                .toList();
    }

    //카테고리로 검색
    @Override
    public List<ProjectListResponse> getByCategory(Category category) {
        return projectRepository.findByCategory(category).stream()
                .map(ProjectListResponse::searchProjects)
                .toList();
    }

    //마감 임박(7일전)
    @Override
    public List<ProjectListResponse> getClosingSoon() {
        return projectRepository.findByLifecycleStatusAndReviewStatusAndEndDateBetween(
                        ProjectLifecycleStatus.LIVE,
                        ProjectReviewStatus.APPROVED,
                        LocalDate.now(),
                        LocalDate.now().plusDays(7)
                ).stream()
                // 한글 설명: 마감 임박 섹션용이므로 badgeClosingSoon = true로 설정.
                .map(p -> ProjectListResponse.fromWithBadges(
                        p,
                        false,  // badgeNew
                        true,   // badgeClosingSoon
                        false,  // badgeSuccessMaker
                        false   // badgeFirstChallengeMaker
                ))
                .toList();
    }


    //프로젝트 상태별 요약
    @Override
    @Transactional(readOnly = true)
    public StatusSummaryResponse getProjectSummary(Long userId) {
        return StatusSummaryResponse.builder()
                .draftCount(count(userId, ProjectLifecycleStatus.DRAFT, ProjectReviewStatus.NONE))            //작성 중
                .reviewCount(count(userId, ProjectLifecycleStatus.DRAFT, ProjectReviewStatus.REVIEW))          //심사 중
                .approvedCount(count(userId, ProjectLifecycleStatus.DRAFT, ProjectReviewStatus.APPROVED))        //승인 됨
                .scheduledCount(count(userId, ProjectLifecycleStatus.SCHEDULED, ProjectReviewStatus.APPROVED))    //공개 예정
                .liveCount(count(userId, ProjectLifecycleStatus.LIVE, ProjectReviewStatus.APPROVED))         //진행 중
                .endCount(count(userId, ProjectLifecycleStatus.ENDED, ProjectReviewStatus.APPROVED))        //종료
                .rejectedCount(count(userId, ProjectLifecycleStatus.DRAFT, ProjectReviewStatus.REJECTED))        //반려됨
                .build();
    }

    //특정 상태 프로젝트 필요한데이터만 조회
    @Override
    public List<?> getProjectByStatus(Long userId, ProjectLifecycleStatus lifecycle, ProjectReviewStatus review) {
        List<Project> projects = projectRepository.findAllByMakerIdAndLifecycleStatusAndReviewStatus(userId, lifecycle, review);

        // 작성중 상태
        if (lifecycle == ProjectLifecycleStatus.DRAFT && review == ProjectReviewStatus.NONE) {
            return projects.stream()
                    .map(TempProjectResponse::from)
                    .toList();
        }

        // 나머지 상태
        return projects.stream()
                .map(project -> switch (project.getReviewStatus()) {
                    case REVIEW -> ProjectListResponse.fromReview(project);
                    case APPROVED -> switch (project.getLifecycleStatus()) {
                        case SCHEDULED -> ProjectListResponse.fromScheduled(project);
                        case LIVE -> ProjectListResponse.fromLive(project);
                        case ENDED -> ProjectListResponse.fromEnded(project);
                        default -> ProjectListResponse.fromApproved(project);
                    };
                    case REJECTED -> ProjectListResponse.fromRejected(project);
                    default -> ProjectListResponse.fromDraft(project); // 위에서 이미 작성중 처리됨(실행안됨)
                })
                .toList();
    }

    private long count(Long userId, ProjectLifecycleStatus lifecycle, ProjectReviewStatus review) {
        return projectRepository.countByMakerIdAndLifecycleStatusAndReviewStatus(userId, lifecycle, review);
    }

    private Maker findMakerByOwnerId(Long ownerUserId) {
        return makerRepository.findByOwner_Id(ownerUserId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "메이커 정보를 찾을 수 없습니다."));
    }
    @Override
    @Transactional(readOnly = true)
    public ProjectDetailResponse getById(Long projectId, Long userId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NoSuchElementException("프로젝트를 찾을 수 없습니다."));

        ProjectDetailResponse response = ProjectDetailResponse.from(project);

        // ✔ follow 도메인에 있는 북마크 서비스 사용
        SupporterProjectBookmarkService.BookmarkStatus status =
                supporterProjectBookmarkService.getStatus(userId, projectId);

        response.setBookmarked(status.bookmarked());
        response.setBookmarkCount(status.bookmarkCount());

        return response;
    }

    // 홈 화면 '지금 뜨는 프로젝트' 섹션용 인기 프로젝트 조회.
    // 조건: LIVE / SCHEDULED + APPROVED 상태만 대상으로 하고,
    // 북마크(찜) 수가 많은 순으로 상위 size개를 반환한다.
    @Override
    @Transactional(readOnly = true)
    public List<TrendingProjectResponse> getTrendingProjects(int size) {
        // 최소/최대 사이즈 방어 코드
        int safeSize = Math.max(1, Math.min(size, 30));

        List<ProjectLifecycleStatus> statuses = List.of(
                ProjectLifecycleStatus.LIVE,
                ProjectLifecycleStatus.SCHEDULED
        );

        return projectRepository.findTrendingProjects(
                statuses,
                ProjectReviewStatus.APPROVED,
                PageRequest.of(0, safeSize)
        );
    }

    // 홈 화면 '방금 업로드된 신규 프로젝트' 섹션용 프로젝트 조회.
    // 기준:
    //  - 최근 3일 이내에 생성된(createdAt) 프로젝트
    //  - 심사 승인 상태(APPROVED)
    //  - 라이프사이클: SCHEDULED(공개 예정), LIVE(진행 중)만 대상
    @Override
    public List<ProjectListResponse> getNewlyUploadedProjects(int size) {
        int safeSize = Math.max(1, Math.min(size, 30));
        LocalDateTime createdAfter = LocalDateTime.now().minusDays(3);
        List<ProjectLifecycleStatus> statuses = List.of(
                ProjectLifecycleStatus.SCHEDULED,
                ProjectLifecycleStatus.LIVE
        );

        return projectRepository.findNewlyUploadedProjects(
                        statuses,
                        ProjectReviewStatus.APPROVED,
                        createdAfter,
                        PageRequest.of(0, safeSize)
                ).stream()
                // 한글 설명: 신규 업로드 섹션이므로 badgeNew = true 로 내려준다.
                .map(p -> ProjectListResponse.fromWithBadges(
                        p,
                        true,   // badgeNew
                        false,  // badgeClosingSoon
                        false,  // badgeSuccessMaker
                        false   // badgeFirstChallengeMaker
                ))
                .toList();
    }


    // 한글 설명: 과거에 성공(SUCCESS)한 프로젝트가 있는 메이커들의
    // 새 프로젝트(공개 예정 / 진행 중)를 조회한다.
    @Override
    public List<ProjectListResponse> getSuccessfulMakersNewProjects(int size) {
        int safeSize = Math.max(1, Math.min(size, 30));

        List<ProjectLifecycleStatus> statuses = List.of(
                ProjectLifecycleStatus.SCHEDULED,
                ProjectLifecycleStatus.LIVE
        );

        return projectRepository.findNewProjectsBySuccessfulMakers(
                        statuses,
                        ProjectReviewStatus.APPROVED,
                        ProjectResultStatus.SUCCESS,
                        PageRequest.of(0, safeSize)
                ).stream()
                // 한글 설명: 성공 메이커 섹션용이므로 badgeSuccessMaker = true.
                .map(p -> ProjectListResponse.fromWithBadges(
                        p,
                        false,  // badgeNew
                        false,  // badgeClosingSoon
                        true,   // badgeSuccessMaker
                        false   // badgeFirstChallengeMaker
                ))
                .toList();
    }

    // 한글 설명: '첫 프로젝트'만 보유한 메이커들의
    // 현재 공개 예정 / 진행 중 프로젝트를 조회한다.
    @Override
    public List<ProjectListResponse> getFirstChallengeMakerProjects(int size) {
        int safeSize = Math.max(1, Math.min(size, 30));

        List<ProjectLifecycleStatus> statuses = List.of(
                ProjectLifecycleStatus.SCHEDULED,
                ProjectLifecycleStatus.LIVE
        );

        return projectRepository.findFirstChallengeMakerProjects(
                        statuses,
                        ProjectReviewStatus.APPROVED,
                        PageRequest.of(0, safeSize)
                ).stream()
                // 한글 설명: 첫 도전 메이커 섹션용이므로 badgeFirstChallengeMaker = true.
                .map(p -> ProjectListResponse.fromWithBadges(
                        p,
                        false,  // badgeNew
                        false,  // badgeClosingSoon
                        false,  // badgeSuccessMaker
                        true    // badgeFirstChallengeMaker
                ))
                .toList();
    }

    // 설명
    // - LIVE + APPROVED 상태 프로젝트를 대상으로,
    //   결제 완료(PAID) 주문 금액 합계를 기반으로 목표 달성률을 계산한다.
    // - 달성률 = (sum(PAID totalAmount) / goalAmount) * 100
    // - 정렬: 달성률 내림차순
    @Override
    @Transactional(readOnly = true)
    public List<ProjectListResponse> getNearGoalProjects(int size) {
        int safeSize = Math.max(1, Math.min(size, 30));

        // 1) LIVE + APPROVED 상태 프로젝트만 후보로 가져온다.
        List<Project> candidates = projectRepository.findByLifecycleStatusAndReviewStatus(
                ProjectLifecycleStatus.LIVE,
                ProjectReviewStatus.APPROVED
        );

        return candidates.stream()
                // 2) goalAmount 없는 프로젝트는 제외
                .filter(p -> p.getGoalAmount() != null && p.getGoalAmount() > 0)
                // 3) 각 프로젝트의 모금액/달성률 계산
                .map(project -> {
                    long fundedAmount = orderRepository
                            .sumTotalAmountByProjectIdAndStatus(project.getId(), OrderStatus.PAID)
                            .orElse(0L);

                    double rate = 0.0;
                    if (project.getGoalAmount() != null && project.getGoalAmount() > 0) {
                        rate = (double) fundedAmount / project.getGoalAmount();
                    }

                    return new ProjectProgress(project, fundedAmount, rate);
                })
                // 4) 아직 한 번도 후원이 안 된 프로젝트는 제외하고 싶으면 여기서 필터
                .filter(pp -> pp.fundedAmount() > 0)
                // 5) 달성률 내림차순 정렬 (가장 목표에 가까운 프로젝트부터)
                .sorted(Comparator.comparing(ProjectProgress::progressRate).reversed())
                // 6) 상위 N개만
                .limit(safeSize)
                // 7) ProjectListResponse로 매핑 + 달성률(%) 세팅
                .map(pp -> {
                    Project project = pp.project();
                    int percentage = (int) Math.floor(pp.progressRate() * 100); // 예: 0.83 -> 83

                    return ProjectListResponse.base(project)
                            .achievementRate(percentage)   // ✅ 여기서만 달성률 채워줌
                            .build();
                })
                .toList();
    }




}
