package com.moa.backend.domain.project.service;

import com.moa.backend.domain.maker.entity.Maker;
import com.moa.backend.domain.maker.repository.MakerRepository;
import com.moa.backend.domain.notification.entity.NotificationType;
import com.moa.backend.domain.notification.service.NotificationService;
import com.moa.backend.domain.project.dto.CreateProject.CreateProjectRequest;
import com.moa.backend.domain.project.dto.CreateProject.CreateProjectResponse;
import com.moa.backend.domain.project.dto.ProjectListResponse;
import com.moa.backend.domain.project.entity.Project;
import com.moa.backend.domain.project.entity.ProjectLifecycleStatus;
import com.moa.backend.domain.project.entity.ProjectReviewStatus;
import com.moa.backend.domain.project.repository.ProjectRepository;
import com.moa.backend.domain.reward.dto.RewardRequest;
import com.moa.backend.domain.reward.factory.RewardFactory;
import com.moa.backend.domain.user.entity.User;
import com.moa.backend.domain.user.repository.UserRepository;
import com.moa.backend.global.error.AppException;
import com.moa.backend.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectCommandServiceImpl implements ProjectCommandService {

    private final ProjectRepository projectRepository;
    private final MakerRepository makerRepository;
    private final RewardFactory rewardFactory;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    // 프로젝트 생성
    @Override
    @Transactional
    public CreateProjectResponse createProject(Long userId, CreateProjectRequest request) {

        Maker maker = makerRepository.findByOwner_Id(userId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));

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
            project.addReward(rewardFactory.createReward(project, r));
        }

        Project saved = projectRepository.save(project);

        // 관리자에게 프로젝트 심사요청 알림
        List<User> admins = userRepository.findByRole("ADMIN");

        admins.forEach(admin -> {
            notificationService.send(
                    admin.getId(),
                    "프로젝트 심사 요청",
                    "[" + project.getTitle() + "] 신규 프로젝트가 생성되어 심사를 요청했습니다.",
                    NotificationType.ADMIN
            );
        });

        return CreateProjectResponse.from(saved);
    }

    //프로젝트 취소(심사중, 승인됨, 공개예정)
    @Override
    public ProjectListResponse canceledProject(Long userId, Long projectId) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new AppException(ErrorCode.PROJECT_NOT_FOUND));

        boolean canCancel =
                (project.getLifecycleStatus() == ProjectLifecycleStatus.DRAFT &&
                        project.getReviewStatus() == ProjectReviewStatus.REVIEW)
                        || (project.getLifecycleStatus() == ProjectLifecycleStatus.DRAFT &&
                        project.getReviewStatus() == ProjectReviewStatus.APPROVED)
                        || (project.getLifecycleStatus() == ProjectLifecycleStatus.SCHEDULED &&
                        project.getReviewStatus() == ProjectReviewStatus.APPROVED);

        if(!canCancel) {
            throw new AppException(ErrorCode.PROJECT_NOT_CANCELED);
        }

        project.setLifecycleStatus(ProjectLifecycleStatus.CANCELED);
        project.setReviewStatus(ProjectReviewStatus.NONE);
        project.setCanceledAt(LocalDateTime.now());

        projectRepository.save(project);

        return ProjectListResponse.fromCanceled(project);
    }
}
