package com.moa.backend.domain.project.scheduler;

import com.moa.backend.domain.follow.repository.SupporterBookmarkProjectRepository;
import com.moa.backend.domain.notification.entity.NotificationType;
import com.moa.backend.domain.notification.service.NotificationService;
import com.moa.backend.domain.order.repository.OrderRepository;
import com.moa.backend.domain.project.entity.Project;
import com.moa.backend.domain.project.entity.ProjectLifecycleStatus;
import com.moa.backend.domain.project.entity.ProjectResultStatus;
import com.moa.backend.domain.project.entity.ProjectReviewStatus;
import com.moa.backend.domain.project.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ProjectStatusScheduler {

    private final ProjectRepository projectRepository;
    private final NotificationService notificationService;
    private final OrderRepository orderRepository;

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void updateProjectStatus() {
        LocalDate today = LocalDate.now();

        // 시작일이 오늘 이후인 프로젝트를 SCHEDULED 으로 변환
        List<Project> scheduled = projectRepository.findByLifecycleStatusAndReviewStatusAndStartDateAfter(
                ProjectLifecycleStatus.DRAFT, ProjectReviewStatus.APPROVED, today
        );

        scheduled.forEach(project -> {
            project.setLifecycleStatus(ProjectLifecycleStatus.SCHEDULED);

            // 메이커에게 공개예정 알림
            Long receiverId = project.getMaker().getOwner().getId();

            notificationService.send(
                    receiverId,
                    "공개 예정 안내",
                    "[" + project.getTitle() + "] 프로젝트가 " + project.getStartDate() + "에 공개됩니다.",
                    NotificationType.MAKER
            );
        });
        projectRepository.saveAll(scheduled);

        //시작일이 오늘인 프로젝트 LIVE로 전환
        List<Project> live = projectRepository.findByLifecycleStatusAndReviewStatusAndStartDate(
                ProjectLifecycleStatus.SCHEDULED, ProjectReviewStatus.APPROVED, today
        );
        live.forEach(project -> {
            project.setLifecycleStatus(ProjectLifecycleStatus.LIVE);

            //메이커에게 LIVE 알림
            Long receiverId = project.getMaker().getOwner().getId();

            notificationService.send(
                    receiverId,
                    "프로젝트 공개",
                    "[" + project.getTitle() + "] 프로젝트가 오늘부터 공개되었습니다!",
                    NotificationType.MAKER
            );
        });
        projectRepository.saveAll(live);

        //시작일이 오늘인 프로젝트 LIVE로 전환 (공개예정 거치지 않고 바로 전환(전날승인했을경우))
        List<Project> lived = projectRepository.findByLifecycleStatusAndReviewStatusAndStartDate(
                ProjectLifecycleStatus.DRAFT, ProjectReviewStatus.APPROVED, today
        );
        lived.forEach(project -> {
            project.setLifecycleStatus(ProjectLifecycleStatus.LIVE);

            //메이커에게 LIVE 알림
            Long receiverId = project.getMaker().getOwner().getId();

            notificationService.send(
                    receiverId,
                    "프로젝트 공개",
                    "[" + project.getTitle() + "] 프로젝트가 오늘부터 바로 공개되었습니다!",
                    NotificationType.MAKER
            );
        });
        projectRepository.saveAll(lived);

        //종료일이 오늘보다 전인 프로젝트 ENDED로 전환
        List<Project> ended = projectRepository.findByLifecycleStatusAndReviewStatusAndEndDateBefore(
                ProjectLifecycleStatus.LIVE, ProjectReviewStatus.APPROVED, today
        );

        ended.forEach(project -> {
            project.setLifecycleStatus(ProjectLifecycleStatus.ENDED);

            // 결제 완료액 총합 조회
            Long fundedAmount = orderRepository.getTotalFundedAmount(project.getId());

            // 목표 달성 여부에 따라 성공/실패 처리
            if (fundedAmount >= project.getGoalAmount()) {
                project.setResultStatus(ProjectResultStatus.SUCCESS);
            } else {
                project.setResultStatus(ProjectResultStatus.FAILED);
            }

            Long receiverId = project.getMaker().getOwner().getId();

            String title, message;

            if (project.getResultStatus() == ProjectResultStatus.SUCCESS) {
                title = "프로젝트 펀딩 성공";
                message = "[" + project.getTitle() + "] 프로젝트가 목표 금액을 달성하며 펀딩이 종료되었습니다!";
            } else {
                title = "프로젝트 펀딩 종료 (미달성)";
                message = "[" + project.getTitle() + "] 목표 금액 미달성으로 펀딩이 종료되었습니다.";
            }

            // 6) 알림 발송
            notificationService.send(
                    receiverId,
                    title,
                    message,
                    NotificationType.MAKER
            );
        });

        projectRepository.saveAll(ended);
    }
}
