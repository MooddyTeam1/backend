package com.moa.backend.domain.project.service;

import com.moa.backend.domain.maker.entity.Maker;
import com.moa.backend.domain.maker.repository.MakerRepository;
import com.moa.backend.domain.project.dto.*;
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

        if (request.getGoalAmount() <=0) {
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

        Project save = projectRepository.save(project);
        return CreateProjectResponse.from(save);
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

    //상태별 조회
    @Override
    public List<ProjectDetailResponse> getByStatus(ProjectLifecycleStatus status) {
        return projectRepository.findByLifecycleStatus(status).stream()
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
                .build();

        Project temp = projectRepository.save(project);
        return TempProjectResponse.from(temp);
    }

    //프로젝트 임시저장 조회
    @Override
    public TempProjectResponse getTempProject(Long userId, Long projectId) {
        Maker maker = findMakerByOwnerId(userId);
        Project project = projectRepository.findByIdAndMaker_Id(projectId, maker.getId())
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));

        return TempProjectResponse.from(project);
    }

    //프로젝트 임시저장 수정
    @Override
    @Transactional
    public TempProjectResponse updateTemp( Long userId, Long projectId, TempProjectRequest request) {
        Maker maker = findMakerByOwnerId(userId);
        Project project = projectRepository.findByIdAndMaker_Id(projectId, maker.getId())
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
    private Maker findMakerByOwnerId(Long ownerUserId) {
        return makerRepository.findByOwner_Id(ownerUserId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "메이커 정보를 찾을 수 없습니다."));
    }
}
