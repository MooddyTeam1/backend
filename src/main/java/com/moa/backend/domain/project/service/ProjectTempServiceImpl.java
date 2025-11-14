package com.moa.backend.domain.project.service;

import com.moa.backend.domain.maker.repository.MakerRepository;
import com.moa.backend.domain.project.dto.CreateProject.CreateProjectRequest;
import com.moa.backend.domain.project.dto.CreateProject.CreateProjectResponse;
import com.moa.backend.domain.project.dto.TempProject.TempProjectRequest;
import com.moa.backend.domain.project.dto.TempProject.TempProjectResponse;
import com.moa.backend.domain.project.entity.Project;
import com.moa.backend.domain.project.entity.ProjectLifecycleStatus;
import com.moa.backend.domain.project.entity.ProjectReviewStatus;
import com.moa.backend.domain.project.repository.ProjectRepository;
import com.moa.backend.domain.reward.dto.RewardRequest;
import com.moa.backend.domain.reward.factory.RewardFactory;
import com.moa.backend.domain.reward.repository.RewardRepository;
import com.moa.backend.domain.user.entity.User;
import com.moa.backend.domain.user.repository.UserRepository;
import com.moa.backend.global.error.AppException;
import com.moa.backend.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectTempServiceImpl implements ProjectTempService {

    private final ProjectRepository projectRepository;
    private final MakerRepository makerRepository;
    private final UserRepository userRepository;
    private final RewardFactory rewardFactory;
    private final RewardRepository rewardRepository;

    //프로젝트 임시 저장 (수정까지같이)
    @Override
    @Transactional
    public TempProjectResponse saveTemp(Long userId, Long projectId, TempProjectRequest request) {
        Project project;

        if (projectId != null) {
            // 기존 임시저장 수정
            project = projectRepository.findByIdAndMaker_Id(projectId, userId)
                    .orElseThrow(() -> new AppException(ErrorCode.PROJECT_NOT_FOUND));

            // 상태 검증
            if (!(project.getLifecycleStatus() == ProjectLifecycleStatus.DRAFT &&
                    (project.getReviewStatus() == ProjectReviewStatus.NONE ||
                            project.getReviewStatus() == ProjectReviewStatus.REJECTED))) {
                throw new AppException(ErrorCode.PROJECT_NOT_EDITABLE);
            }
        } else {
            // 새로 생성
            project = Project.builder()
                    .maker(makerRepository.findByOwner_Id(userId)
                            .orElseThrow(() -> new AppException(ErrorCode.FORBIDDEN)))
                    .lifecycleStatus(ProjectLifecycleStatus.DRAFT)
                    .reviewStatus(ProjectReviewStatus.NONE)
                    .build();
        }

        // 입력된 필드만 업데이트 (null 이면 기존값 유지)
        if (request.getTitle() != null) project.setTitle(request.getTitle());
        if (request.getSummary() != null) project.setSummary(request.getSummary());
        if (request.getGoalAmount() != null) project.setGoalAmount(request.getGoalAmount());
        if (request.getCategory() != null) project.setCategory(request.getCategory());
        if (request.getStartDate() != null) project.setStartDate(request.getStartDate());
        if (request.getEndDate() != null) project.setEndDate(request.getEndDate());
        if (request.getStoryMarkdown() != null) project.setStoryMarkdown(request.getStoryMarkdown());
        if (request.getCoverImageUrl() != null) project.setCoverImageUrl(request.getCoverImageUrl());

        if (projectId == null) {
            projectRepository.save(project);
        }

        // 기존 리워드 전체 삭제
        rewardRepository.deleteByProject(project);
        project.getRewards().clear();

        // 새로운 리워드 추가
        if (request.getRewardRequests() != null) {
            for (RewardRequest r : request.getRewardRequests()) {
                project.addReward(rewardFactory.createReward(project, r));
            }
        }

        projectRepository.save(project);

        return TempProjectResponse.from(project);
    }


    // 심사 요청
    @Override
    @Transactional
    public CreateProjectResponse requestTemp(Long userId, Long projectId, CreateProjectRequest request) {
        Project project = projectRepository.findByIdAndMaker_Id(projectId, userId)
                .orElseThrow(() -> new AppException(ErrorCode.PROJECT_NOT_FOUND));

        // 상태 검증
        if (!(project.getLifecycleStatus() == ProjectLifecycleStatus.DRAFT &&
                (project.getReviewStatus() == ProjectReviewStatus.NONE ||
                        project.getReviewStatus() == ProjectReviewStatus.REJECTED))) {
            throw new AppException(ErrorCode.PROJECT_NOT_EDITABLE);
        }

        // 빈칸 검증
        if (project.getTitle() == null || project.getTitle().trim().isEmpty() ||
                project.getSummary() == null || project.getSummary().trim().isEmpty() ||
                project.getGoalAmount() == null || project.getGoalAmount() <= 0 ||
                project.getStartDate() == null ||
                project.getEndDate() == null ||
                project.getCategory() == null) {
            throw new AppException(ErrorCode.PROJECT_NOT_REQUEST);
        }

        // 상태 전환
        project.setReviewStatus(ProjectReviewStatus.REVIEW);
        project.setRequestAt(LocalDateTime.now());

        return CreateProjectResponse.from(project);
    }

    //임시저장 프로젝트 삭제
    @Override
    @Transactional
    public void deleteTemp(Long userId, Long projectId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.PROJECT_NOT_EDITABLE));

        Project project = projectRepository.findByIdAndMaker_Id(projectId, userId)
                .orElseThrow(() -> new AppException(ErrorCode.PROJECT_NOT_FOUND));

        if(!(project.getLifecycleStatus() == ProjectLifecycleStatus.DRAFT &&
                project.getReviewStatus() == ProjectReviewStatus.NONE)) {
            throw new AppException(ErrorCode.PROJECT_NOT_DELETE);
        }

        projectRepository.delete(project);
    }
}
