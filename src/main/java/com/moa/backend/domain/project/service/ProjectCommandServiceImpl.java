package com.moa.backend.domain.project.service;

import com.moa.backend.domain.maker.entity.Maker;
import com.moa.backend.domain.maker.repository.MakerRepository;
import com.moa.backend.domain.project.dto.CreateProjectRequest;
import com.moa.backend.domain.project.dto.CreateProjectResponse;
import com.moa.backend.domain.project.entity.Project;
import com.moa.backend.domain.project.entity.ProjectLifecycleStatus;
import com.moa.backend.domain.project.entity.ProjectReviewStatus;
import com.moa.backend.domain.project.repository.ProjectRepository;
import com.moa.backend.domain.reward.dto.RewardRequest;
import com.moa.backend.domain.reward.factory.RewardFactory;
import com.moa.backend.global.error.AppException;
import com.moa.backend.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly=true)
public class ProjectCommandServiceImpl implements ProjectCommandService{

    private final ProjectRepository projectRepository;
    private final MakerRepository makerRepository;
    private final RewardFactory rewardFactory;

    // 프로젝트 생성
    @Override
    @Transactional
    public CreateProjectResponse createProject(Long userId, CreateProjectRequest request) {

        Maker maker = makerRepository.findByOwner_Id(userId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "메이커 정보를 찾을 수 없습니다."));

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
        return CreateProjectResponse.from(saved);
    }
}
