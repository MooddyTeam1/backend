package com.moa.backend.domain.project.service;

import com.moa.backend.domain.maker.entity.Maker;
import com.moa.backend.domain.maker.repository.MakerRepository;
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

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final MakerRepository makerRepository;

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
                ).stream().map(ProjectListResponse::searchProjects)
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
}
