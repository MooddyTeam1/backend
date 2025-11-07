package com.moa.backend.domain.project.service;

import com.moa.backend.domain.project.dto.CreateProjectRequest;
import com.moa.backend.domain.project.dto.CreateProjectResponse;
import com.moa.backend.domain.project.dto.ProjectDetailResponse;
import com.moa.backend.domain.project.entity.Category;
import com.moa.backend.domain.project.entity.Project;
import com.moa.backend.domain.project.entity.ProjectLifecycleStatus;
import com.moa.backend.domain.project.entity.ProjectReviewStatus;
import com.moa.backend.domain.project.repository.ProjectRepository;
import com.moa.backend.domain.user.entity.User;
import com.moa.backend.domain.user.repository.UserRepository;
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
    private final UserRepository userRepository;

    // 프로젝트 생성
    @Override
    @Transactional
    public CreateProjectResponse createProject(Long userId, CreateProjectRequest request) {

        if (userId == null) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));

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
                .maker(user)
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

}
