package com.moa.backend.domain.project.service;

import com.moa.backend.domain.project.dto.ProjectRequest;
import com.moa.backend.domain.project.dto.ProjectResponse;
import com.moa.backend.domain.project.entity.Category;
import com.moa.backend.domain.project.entity.Project;
import com.moa.backend.domain.project.entity.ProjectStatus;
import com.moa.backend.domain.project.repository.ProjectRepository;
import com.moa.backend.domain.user.entity.CreatorStatus;
import com.moa.backend.domain.user.entity.User;
import com.moa.backend.domain.user.repository.UserRepository;
import com.moa.backend.global.error.AppException;
import com.moa.backend.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly=true)
public class ProjectServiceImpl implements ProjectService{

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    // 프로젝트 등록
    @Override
    @Transactional
    public ProjectResponse createProject(Long userId, ProjectRequest request) {

        if (userId == null) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));

        if (user.getCreatorStatus() != CreatorStatus.APPROVED) {
            throw new AppException(ErrorCode.UNAUTHORIZED_CREATOR);
        }

        if (request.getGoalAmount() <=0) {
            throw new AppException(ErrorCode.INVALID_AMOUNT);
        }

        if (request.getStartAt().isAfter(request.getEndAt())) {
            throw new AppException(ErrorCode.INVALID_DATE);
        }

        if (projectRepository.existsByTitle(request.getTitle())) {
            throw new AppException(ErrorCode.PROJECT_DUPLICATE_TITLE);
        }

        Project project = Project.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .goalAmount(request.getGoalAmount())
                .startAt(request.getStartAt())
                .endAt(request.getEndAt())
                .category(request.getCategory())
                .thumbnailUrl(request.getThumbnailUrl())
                .creator(user)
                .build();

        Project save = projectRepository.save(project);
        return ProjectResponse.from(save);
    }

    //프로젝트 전체조회
    @Override
    public List<ProjectResponse> getAll() {
        return projectRepository.findAll().stream()
                .map(ProjectResponse::from)
                .collect(Collectors.toList());
    }

    //프로젝트 단일 조회
    @Override
    public ProjectResponse getById(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("프로젝트을 찾을 수없습니다. id=" + id));

        return ProjectResponse.from(project);
    }

    //제목 검색
    @Override
    public List<ProjectResponse> searchByTitle(String keyword) {
        return projectRepository.searchByTitle(keyword).stream()
                .map(ProjectResponse::from)
                .collect(Collectors.toList());
    }

    //상태별 조회
    @Override
    public List<ProjectResponse> getByStatus(ProjectStatus status) {
        return projectRepository.findByStatus(status).stream()
                .map(ProjectResponse::from)
                .collect(Collectors.toList());
    }

    //카테고리별 조회
    @Override
    public List<ProjectResponse> getByCategory(Category category) {
        return projectRepository.findByCategory(category).stream()
                .map(ProjectResponse::from)
                .collect(Collectors.toList());
    }

    //프로젝트 삭제
    @Override
    @Transactional
    public ProjectResponse deleteProject(Long userId, Long projectId) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new AppException(ErrorCode.PROJECT_NOT_FOUND));

        if (!project.getCreator().getId().equals(userId)) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }

        if (project.isInProgress()) {
            throw new AppException(ErrorCode.PROJECT_CANNOT_DELETE_IN_PROGRESS);
        }

        projectRepository.delete(project);
        return ProjectResponse.from(project);
    }
}