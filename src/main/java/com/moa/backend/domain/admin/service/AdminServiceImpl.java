package com.moa.backend.domain.admin.service;

import com.moa.backend.domain.notification.entity.NotificationTargetType;
import com.moa.backend.domain.notification.entity.NotificationType;
import com.moa.backend.domain.notification.service.NotificationService;
import com.moa.backend.domain.project.dto.CreateProject.CreateProjectResponse;
import com.moa.backend.domain.project.dto.ProjectDetailResponse;
import com.moa.backend.domain.admin.dto.ProjectStatusResponse;
import com.moa.backend.domain.project.entity.Project;
import com.moa.backend.domain.project.entity.ProjectLifecycleStatus;
import com.moa.backend.domain.project.entity.ProjectReviewStatus;
import com.moa.backend.domain.project.repository.ProjectRepository;
import com.moa.backend.domain.wallet.service.ProjectWalletService;
import com.moa.backend.global.error.AppException;
import com.moa.backend.global.error.ErrorCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminServiceImpl implements AdminService {

    private final ProjectRepository projectRepository;
    private final ProjectWalletService projectWalletService;
    private final NotificationService notificationService;


    //프로젝트 승인
    @Override
    public ProjectStatusResponse approveProject(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new AppException(ErrorCode.PROJECT_NOT_FOUND));

        validateProjectStatusChangeable(project);

        project.setApprovedAt(LocalDateTime.now());

        //시작일이 오늘이거나 오늘 이전인 프로젝트 진행중 상태로 변환
        LocalDate today =  LocalDate.now();
        if (project.getStartDate().isEqual(today) || project.getStartDate().isBefore(today)) {
            project.setReviewStatus(ProjectReviewStatus.APPROVED);
            project.setLifecycleStatus(ProjectLifecycleStatus.LIVE);
        } else {    // 시작일이 미래인 프로젝트는 승인됨 상태로 변환
            project.setLifecycleStatus(ProjectLifecycleStatus.DRAFT);
            project.setReviewStatus(ProjectReviewStatus.APPROVED);
        }

        projectRepository.save(project);
        projectWalletService.createForProject(project);

        // 메이커에게 승인 알림
        Long makerUserId = project.getMaker().getOwner().getId();

        notificationService.send(
                makerUserId,
                "프로젝트 심사 승인",
                "[" + project.getTitle() + "] 의 프로젝트가 심사에서 승인되었습니다.",
                NotificationType.MAKER,
                NotificationTargetType.PROJECT,
                project.getId()
        );

        return ProjectStatusResponse.from(project);
    }

    //프로젝트 반려
    @Override
    @Transactional
    public ProjectStatusResponse rejectProject(Long projectId, String reason) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new AppException(ErrorCode.PROJECT_NOT_FOUND));

        boolean canReject =
                (project.getLifecycleStatus() == ProjectLifecycleStatus.DRAFT &&
                        project.getReviewStatus() == ProjectReviewStatus.REVIEW)
                        || (project.getLifecycleStatus() == ProjectLifecycleStatus.DRAFT &&
                        project.getReviewStatus() == ProjectReviewStatus.APPROVED)
                        || (project.getLifecycleStatus() == ProjectLifecycleStatus.SCHEDULED &&
                        project.getReviewStatus() == ProjectReviewStatus.APPROVED);

        if (!canReject) {
            throw new AppException(ErrorCode.PROJECT_NOT_REJECTABLE);
        }

        project.setLifecycleStatus(ProjectLifecycleStatus.DRAFT);
        project.setReviewStatus(ProjectReviewStatus.REJECTED);
        project.setRejectedReason(reason);
        project.setRejectedAt(LocalDateTime.now());

        projectRepository.save(project);

        // 메이커에게 반려 알림
        Long makerUserId = project.getMaker().getOwner().getId();

        notificationService.send(
                makerUserId,
                "프로젝트 심사 거절",
                "[" + project.getTitle() + "] 프로젝트 심사가 거절되었습니다.\n사유: " + reason,
                NotificationType.MAKER,
                NotificationTargetType.PROJECT,
                project.getId()
        );

        return ProjectStatusResponse.from(project);
    }

    //프로젝트 승인대기 조회
    @Override
    public List<CreateProjectResponse> getReviewProjects() {
        return projectRepository.findByLifecycleStatusAndReviewStatus(
                ProjectLifecycleStatus.DRAFT,
                ProjectReviewStatus.REVIEW
                )
                .stream()
                .map(CreateProjectResponse::from)
                .collect(Collectors.toList());
    }

    //프로젝트 승인대기조회(검토페이지)
    @Override
    public ProjectDetailResponse getProjectDetailsReview(Long projectId) {
        Project project = projectRepository.findByIdAndLifecycleStatusAndReviewStatus(
                        projectId,
                        ProjectLifecycleStatus.DRAFT,
                        ProjectReviewStatus.REVIEW
                )
                .orElseThrow(() -> new AppException(ErrorCode.PROJECT_NOT_FOUND));

        return ProjectDetailResponse.from(project);
    }

    private void validateProjectStatusChangeable(Project project) {
        if (project.getLifecycleStatus() == ProjectLifecycleStatus.LIVE) {
            throw new AppException(ErrorCode.PROJECT_ALREADY_FUNDING);
        }
        if (project.getLifecycleStatus() == ProjectLifecycleStatus.SCHEDULED) {
            throw new AppException(ErrorCode.PROJECT_ALREADY_SUCCESS);
        }
        if (project.getLifecycleStatus() == ProjectLifecycleStatus.ENDED) {
            throw new AppException(ErrorCode.PROJECT_ALREADY_ENDED);
        }
    }
}
