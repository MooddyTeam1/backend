package com.moa.backend.domain.project.service;

import com.moa.backend.domain.maker.entity.Maker;
import com.moa.backend.domain.maker.repository.MakerRepository;
import com.moa.backend.domain.project.dto.*;
import com.moa.backend.domain.project.entity.*;
import com.moa.backend.domain.project.repository.ProjectRepository;
import com.moa.backend.domain.reward.dto.select.OptionGroupRequest;
import com.moa.backend.domain.reward.dto.select.OptionValueRequest;
import com.moa.backend.domain.reward.dto.RewardRequest;
import com.moa.backend.domain.reward.dto.set.SetRewardRequest;
import com.moa.backend.domain.reward.entity.OptionGroup;
import com.moa.backend.domain.reward.entity.OptionValue;
import com.moa.backend.domain.reward.entity.Reward;
import com.moa.backend.domain.reward.entity.RewardSet;
import com.moa.backend.global.error.AppException;
import com.moa.backend.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly=true)
public class ProjectServiceImpl implements ProjectService{

    private final ProjectRepository projectRepository;
    private final MakerRepository makerRepository;

    // 프로젝트 생성
    @Override
    @Transactional
    public CreateProjectResponse createProject(Long userId, CreateProjectRequest request) {

        if (userId == null) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        Maker maker = findMakerByOwnerId(userId);

        if (request.getGoalAmount() <= 0) {
            throw new AppException(ErrorCode.INVALID_AMOUNT);
        }

        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new AppException(ErrorCode.INVALID_DATE);
        }

        if (projectRepository.existsByTitle(request.getTitle())) {
            throw new AppException(ErrorCode.PROJECT_DUPLICATE_TITLE);
        }

        Project project = Project.builder()
                .title(request.getTitle())
                .summary(request.getSummary())
                .storyMarkdown(request.getStoryMarkdown())
                .goalAmount(request.getGoalAmount())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .category(request.getCategory())
                .lifecycleStatus(ProjectLifecycleStatus.DRAFT)
                .reviewStatus(ProjectReviewStatus.REVIEW)
                .requestAt(LocalDateTime.now())
                .coverImageUrl(request.getCoverImageUrl())
                .coverGallery(request.getCoverGallery())
                .tags(request.getTags())
                .maker(maker)
                .build();

        for (RewardRequest r : request.getRewardRequests()) {
            Reward reward = createReward(project, r);
            project.getRewards().add(reward);
        }

        Project saved = projectRepository.save(project);
        return CreateProjectResponse.from(saved);

    }

    //프로젝트 전체조회
    @Override
    public List<ProjectDetailResponse> getAll() {
        return projectRepository.findAll().stream()
                .map(ProjectDetailResponse::from)
                .collect(Collectors.toList());
    }

    //프로젝트 단일 조회
    @Override
    public ProjectDetailResponse getById(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("프로젝트을 찾을 수없습니다. id=" + id));

        return ProjectDetailResponse.from(project);
    }

    //제목 검색
    @Override
    public List<ProjectDetailResponse> searchByTitle(String keyword) {
        return projectRepository.searchByTitle(keyword).stream()
                .map(ProjectDetailResponse::from)
                .collect(Collectors.toList());
    }

    //카테고리별 조회
    @Override
    public List<ProjectDetailResponse> getByCategory(Category category) {
        return projectRepository.findByCategory(category).stream()
                .map(ProjectDetailResponse::from)
                .collect(Collectors.toList());
    }

    //프로젝트 임시 저장
    @Override
    @Transactional
    public TempProjectResponse saveTemp(Long userId, TempProjectRequest request) {
        Maker maker = findMakerByOwnerId(userId);

        Project project = Project.builder()
                .title(request.getTitle())
                .summary(request.getSummary())
                .storyMarkdown(request.getStoryMarkdown())
                .goalAmount(request.getGoalAmount())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .category(request.getCategory())
                .lifecycleStatus(ProjectLifecycleStatus.DRAFT)
                .reviewStatus(ProjectReviewStatus.NONE)
                .coverImageUrl(request.getCoverImageUrl())
                .coverGallery(request.getCoverGallery())
                .tags(request.getTags())
                .maker(maker)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Project temp = projectRepository.save(project);
        return TempProjectResponse.from(temp);
    }

    //프로젝트 임시저장 수정
    @Override
    @Transactional
    public TempProjectResponse updateTemp(Long userId, Long projectId, TempProjectRequest request) {
        Project project = projectRepository.findByIdAndMaker_Id(projectId, userId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));

        if(!(project.getLifecycleStatus() == ProjectLifecycleStatus.DRAFT &&
                (project.getReviewStatus() == ProjectReviewStatus.NONE ||
                  project.getReviewStatus() == ProjectReviewStatus.REJECTED))){
            throw new AppException(ErrorCode.PROJECT_NOT_EDITABLE);
        }

        if (request.getTitle() != null) project.setTitle(request.getTitle());
        if (request.getSummary() != null) project.setSummary(request.getSummary());
        if (request.getStoryMarkdown() != null) project.setStoryMarkdown(request.getStoryMarkdown());
        if (request.getGoalAmount() != null) project.setGoalAmount(request.getGoalAmount());
        if (request.getStartDate() != null) project.setStartDate(request.getStartDate());
        if (request.getEndDate() != null) project.setEndDate(request.getEndDate());
        if (request.getCategory() != null) project.setCategory(request.getCategory());
        if (request.getCoverImageUrl() != null) project.setCoverImageUrl(request.getCoverImageUrl());
        if (request.getCoverGallery() != null) project.setCoverGallery(request.getCoverGallery());
        if (request.getTags() != null) project.setTags(request.getTags());

        Project temp = projectRepository.save(project);
        return TempProjectResponse.from(temp);
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

    private Reward createReward(Project project, RewardRequest r) {
        Reward reward = Reward.builder()
                .project(project)
                .name(r.getName())
                .description(r.getDescription())
                .stockQuantity(r.getStockQuantity())
                .price(r.getPrice())
                .estimatedDeliveryDate(r.getEstimatedDeliveryDate())
                .active(r.isActive())
                .build();

        // 옵션 그룹 처리
        if (!CollectionUtils.isEmpty(r.getOptionGroups())) {
            r.getOptionGroups().forEach(g -> reward.addOptionGroup(toOptionGroup(g)));
        }

        // 세트 리워드 처리
        if (!CollectionUtils.isEmpty(r.getSetRewards())) {
            r.getSetRewards().forEach(s -> reward.addRewardSet(toRewardSet(s)));
        }

        return reward;
    }

    private OptionGroup toOptionGroup(OptionGroupRequest g) {
        OptionGroup group = OptionGroup.builder()
                .groupName(g.getGroupName())
                .build();

        if (!CollectionUtils.isEmpty(g.getOptionValues())) {
            g.getOptionValues().forEach(v -> {
                OptionValue value = OptionValue.builder()
                        .optionValue(v.getOptionValue())
                        .addPrice(v.getAddPrice())
                        .stockQuantity(v.getStockQuantity())
                        .build();
                group.addOptionValue(value);
            });
        }
        return group;
    }

    private RewardSet toRewardSet(SetRewardRequest s) {
        RewardSet rewardSet = RewardSet.builder()
                .setName(s.getSetName())
                .stockQuantity(s.getStockQuantity())
                .build();

        if (!CollectionUtils.isEmpty(s.getOptionGroups())) {
            s.getOptionGroups().forEach(g -> rewardSet.addOptionGroup(toOptionGroup(g)));
        }
        return rewardSet;
    }
}
