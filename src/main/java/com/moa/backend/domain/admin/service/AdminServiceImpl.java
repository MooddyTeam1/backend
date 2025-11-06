package com.moa.backend.domain.admin.service;

import com.moa.backend.domain.admin.dto.UserResponse;
import com.moa.backend.domain.project.dto.ProjectResponse;
import com.moa.backend.domain.project.dto.ProjectStatusResponse;
import com.moa.backend.domain.project.entity.Project;
import com.moa.backend.domain.project.entity.ProjectStatus;
import com.moa.backend.domain.project.repository.ProjectRepository;
import com.moa.backend.domain.user.entity.CreatorStatus;
import com.moa.backend.domain.user.entity.User;
import com.moa.backend.domain.user.repository.UserRepository;
import com.moa.backend.global.error.AppException;
import com.moa.backend.global.error.ErrorCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminServiceImpl implements AdminService {

    private final ProjectRepository projectRepository;


    //프로젝트 승인
    @Override
    public ProjectStatusResponse approveProject(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new AppException(ErrorCode.PROJECT_NOT_FOUND));

        validateProjectStatusChangeable(project);

        project.setStatus(ProjectStatus.FUNDING);
        project.setApprovedAt(LocalDateTime.now());

        projectRepository.save(project);

        return ProjectStatusResponse.from(project);
    }

    //프로젝트 반려
    @Override
    public ProjectStatusResponse rejectProject(Long projectId, String reason) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new AppException(ErrorCode.PROJECT_NOT_FOUND));

        validateProjectStatusChangeable(project);

        project.setStatus(ProjectStatus.FAILED);
        project.setRejectedAt(LocalDateTime.now());
        project.setRejectionReason(reason);

        projectRepository.save(project);

        return ProjectStatusResponse.from(project);
    }

    //프로젝트 승인대기 조회
    @Override
    public List<ProjectResponse> getDraftProjects() {
        return projectRepository.findByStatus(ProjectStatus.DRAFT)
                .stream()
                .map(ProjectResponse::from)
                .collect(Collectors.toList());
    }

    private void validateProjectStatusChangeable(Project project) {
        if (project.getStatus() == ProjectStatus.FUNDING) {
            throw new AppException(ErrorCode.PROJECT_ALREADY_FUNDING);
        }
        if (project.getStatus() == ProjectStatus.SUCCESS) {
            throw new AppException(ErrorCode.PROJECT_ALREADY_SUCCESS);
        }
        if (project.getStatus() == ProjectStatus.FAILED) {
            throw new AppException(ErrorCode.PROJECT_ALREADY_FAILED);
        }
    }
}
