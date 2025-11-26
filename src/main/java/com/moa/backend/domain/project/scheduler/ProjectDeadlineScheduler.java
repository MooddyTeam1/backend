package com.moa.backend.domain.project.scheduler;

import com.moa.backend.domain.follow.repository.SupporterBookmarkProjectRepository;
import com.moa.backend.domain.notification.entity.NotificationTargetType;
import com.moa.backend.domain.notification.entity.NotificationType;
import com.moa.backend.domain.notification.service.NotificationService;
import com.moa.backend.domain.project.entity.Project;
import com.moa.backend.domain.project.entity.ProjectLifecycleStatus;
import com.moa.backend.domain.project.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Component
public class ProjectDeadlineScheduler {

    private final ProjectRepository projectRepository;
    private final NotificationService notificationService;
    private final SupporterBookmarkProjectRepository supporterBookmarkProjectRepository;

    /**
     * 마감 임박 알림 (D-3 / D-1)
     */
    @Scheduled(cron = "0 0 9 * * *") // 매일 오전 9시 실행
    @Transactional
    public void sendClosingSoonNotification() {
        LocalDate today = LocalDate.now();

        // D-3 (3일 전) → 메이커에게만
        LocalDate d3 = today.plusDays(3);
        List<Project> d3Projects = projectRepository.findByLifecycleStatusAndEndDate(
                ProjectLifecycleStatus.LIVE, d3
        );

        d3Projects.forEach(project -> {
            Long makerId = project.getMaker().getOwner().getId();
            notificationService.send(
                    makerId,
                    "마감 임박 (D-3)",
                    "[" + project.getTitle() + "] 펀딩 종료 3일 전입니다. 홍보를 진행해보세요!",
                    NotificationType.MAKER,
                    NotificationTargetType.PROJECT,
                    project.getId()
            );
        });

        // D-1 (1일 전) → 메이커 + 북마크 서포터
        LocalDate d1 = today.plusDays(1);
        List<Project> d1Projects = projectRepository.findByLifecycleStatusAndEndDate(
                ProjectLifecycleStatus.LIVE, d1
        );

        d1Projects.forEach(project -> {
            Long makerId = project.getMaker().getOwner().getId();
            // 메이커 알림
            notificationService.send(
                    makerId,
                    "마감 임박 (D-1)",
                    "[" + project.getTitle() + "] 펀딩이 내일 종료됩니다. 마지막 홍보를 진행하세요!",
                    NotificationType.MAKER,
                    NotificationTargetType.PROJECT,
                    project.getId()
            );

            // 이 프로젝트를 북마크한 서포터들만 알림
            List<Long> supporterIds =
                    supporterBookmarkProjectRepository.findSupporterIdsByProject(project.getId());

            if (!supporterIds.isEmpty()) {
                supporterIds.forEach(userId -> notificationService.send(
                        userId,
                        "마감 임박 (D-1)",
                        "[" + project.getTitle() + "] 펀딩이 곧 종료됩니다! 놓치지 마세요!",
                        NotificationType.SUPPORTER,
                        NotificationTargetType.PROJECT,
                        project.getId()
                ));
            }
        });
    }
}

