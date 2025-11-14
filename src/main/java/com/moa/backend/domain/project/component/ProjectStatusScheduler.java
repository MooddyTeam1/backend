package com.moa.backend.domain.project.component;

import com.moa.backend.domain.project.entity.Project;
import com.moa.backend.domain.project.entity.ProjectLifecycleStatus;
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

    @Scheduled(cron = "0/20 * * * * *")
    @Transactional
    public void updateProjectStatus(){
        LocalDate today = LocalDate.now();

        // 시작일이 오늘 이후인 프로젝트를 SCHEDULED 으로 변환
        List<Project> scheduled = projectRepository.findByLifecycleStatusAndReviewStatusAndStartDateAfter(
                ProjectLifecycleStatus.DRAFT, ProjectReviewStatus.APPROVED, today
        );

        scheduled.forEach(project -> {
            project.setLifecycleStatus(ProjectLifecycleStatus.SCHEDULED);
        });
        projectRepository.saveAll(scheduled);

        //시작일이 오늘인 프로젝트 LIVE로 전환
        List<Project> live = projectRepository.findByLifecycleStatusAndReviewStatusAndStartDate(
                ProjectLifecycleStatus.SCHEDULED, ProjectReviewStatus.APPROVED, today
        );
        live.forEach(project -> {
            project.setLifecycleStatus(ProjectLifecycleStatus.LIVE);
        });
        projectRepository.saveAll(live);

        //종료일이 오늘보다 전인 프로젝트 ENDED로 전환
        List<Project> ended = projectRepository.findByLifecycleStatusAndReviewStatusAndEndDateBefore(
                ProjectLifecycleStatus.LIVE, ProjectReviewStatus.APPROVED, today
        );

        ended.forEach(project -> {
            project.setLifecycleStatus(ProjectLifecycleStatus.ENDED);
        });
        projectRepository.saveAll(ended);
    }
}
