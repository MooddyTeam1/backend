package com.moa.backend.domain.project.service;

import com.moa.backend.domain.maker.entity.Maker;
import com.moa.backend.domain.maker.repository.MakerRepository;
import com.moa.backend.domain.project.dto.TempProjectRequest;
import com.moa.backend.domain.project.dto.TempProjectResponse;
import com.moa.backend.domain.project.entity.Project;
import com.moa.backend.domain.project.entity.ProjectLifecycleStatus;
import com.moa.backend.domain.project.entity.ProjectReviewStatus;
import com.moa.backend.domain.project.repository.ProjectRepository;
import com.moa.backend.global.error.AppException;
import com.moa.backend.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly=true)
public class ProjectTempServiceImpl implements  ProjectTempService {

    private final ProjectRepository projectRepository;
    private final MakerRepository makerRepository;

    //프로젝트 임시 저장
    @Override
    @Transactional
    public TempProjectResponse saveTemp(Long userId, TempProjectRequest request) {

        Maker maker = makerRepository.findByOwner_Id(userId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "메이커 정보를 찾을 수 없습니다."));
        
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
}
