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
import com.moa.backend.global.error.AppException;
import com.moa.backend.global.error.ErrorCode;
import jakarta.persistence.EntityManager;
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
    private final RewardFactory rewardFactory;
    private final RewardRepository rewardRepository;
    private final EntityManager entityManager;

    // 프로젝트 임시 저장 (수정까지같이)
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
        if (request.getTitle() != null)
            project.setTitle(request.getTitle());
        if (request.getSummary() != null)
            project.setSummary(request.getSummary());
        if (request.getGoalAmount() != null)
            project.setGoalAmount(request.getGoalAmount());
        if (request.getCategory() != null)
            project.setCategory(request.getCategory());
        if (request.getStartDate() != null)
            project.setStartDate(request.getStartDate());
        if (request.getEndDate() != null)
            project.setEndDate(request.getEndDate());
        if (request.getStoryMarkdown() != null)
            project.setStoryMarkdown(request.getStoryMarkdown());
        if (request.getCoverImageUrl() != null)
            project.setCoverImageUrl(request.getCoverImageUrl());
        // 한글 설명: coverGallery와 tags 필드 저장 추가
        if (request.getCoverGallery() != null)
            project.setCoverGallery(request.getCoverGallery());
        if (request.getTags() != null)
            project.setTags(request.getTags());

        if (projectId == null) {
            projectRepository.save(project);
        }

        // 기존 리워드 전체 삭제
        rewardRepository.deleteByProject(project);
        project.getRewards().clear();
        // 한글 설명: 삭제를 즉시 DB에 반영하여 새 리워드 추가 전 영속성 컨텍스트 동기화
        entityManager.flush();

        // 새로운 리워드 추가
        if (request.getRewardRequests() != null) {
            for (RewardRequest r : request.getRewardRequests()) {
                project.addReward(rewardFactory.createReward(project, r));
            }
        }

        projectRepository.save(project);

        // 한글 설명: disclosure 데이터가 DB에 저장되도록 명시적으로 flush
        // 한글 설명: 영속성 컨텍스트를 초기화하여 fetch join이 제대로 작동하도록 함
        entityManager.flush();
        entityManager.clear();

        // 한글 설명: 응답 반환 전에 rewards를 명시적으로 로드하기 위해 fetch join으로 다시 조회
        Project savedProject = projectRepository.findByIdWithRewardsAndMaker(project.getId())
                .orElseThrow(() -> new AppException(ErrorCode.PROJECT_NOT_FOUND));

        return TempProjectResponse.from(savedProject);
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

        // 한글 설명: 리워드 목록을 fetch join으로 조회하여 검증
        Project projectWithRewards = projectRepository.findByIdWithRewardsAndMaker(project.getId())
                .orElseThrow(() -> new AppException(ErrorCode.PROJECT_NOT_FOUND));

        // 한글 설명: 리워드 필수 필드 검증 (심사 요청 시 필수)
        if (projectWithRewards.getRewards() == null || projectWithRewards.getRewards().isEmpty()) {
            throw new AppException(ErrorCode.REWARD_REQUIRED);
        }

        // 한글 설명: 각 리워드의 필수 필드(name, description, price) 검증
        for (var reward : projectWithRewards.getRewards()) {
            if (reward.getName() == null || reward.getName().trim().isEmpty()) {
                throw new AppException(ErrorCode.PROJECT_NOT_REQUEST, "리워드 이름은 필수입니다.");
            }
            if (reward.getDescription() == null || reward.getDescription().trim().isEmpty()) {
                throw new AppException(ErrorCode.PROJECT_NOT_REQUEST, "리워드 설명은 필수입니다.");
            }
            if (reward.getPrice() == null || reward.getPrice() <= 0) {
                throw new AppException(ErrorCode.PROJECT_NOT_REQUEST, "리워드 가격은 0보다 커야 합니다.");
            }
        }

        // 상태 전환
        project.setReviewStatus(ProjectReviewStatus.REVIEW);
        project.setRequestAt(LocalDateTime.now());

        return CreateProjectResponse.from(project);
    }

    // 임시저장 프로젝트 삭제
    @Override
    @Transactional
    public void deleteTemp(Long userId, Long projectId) {
        Project project = projectRepository.findByIdAndMaker_Id(projectId, userId)
                .orElseThrow(() -> new AppException(ErrorCode.PROJECT_NOT_FOUND));

        if (!(project.getLifecycleStatus() == ProjectLifecycleStatus.DRAFT &&
                project.getReviewStatus() == ProjectReviewStatus.NONE)) {
            throw new AppException(ErrorCode.PROJECT_NOT_DELETE);
        }

        projectRepository.delete(project);
    }
}
