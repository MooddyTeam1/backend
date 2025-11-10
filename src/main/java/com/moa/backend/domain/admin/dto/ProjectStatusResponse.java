package com.moa.backend.domain.admin.dto;

import com.moa.backend.domain.project.entity.Project;
import com.moa.backend.domain.project.entity.ProjectLifecycleStatus;
import com.moa.backend.domain.project.entity.ProjectReviewStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProjectStatusResponse {
    private Long id;
    private ProjectLifecycleStatus lifecycleStatus;
    private ProjectReviewStatus reviewStatus;
    private LocalDateTime approvedAt;
    private LocalDateTime rejectedAt;
    private String rejectedReason;

    public static ProjectStatusResponse from(Project project) {
        return ProjectStatusResponse.builder()
                .id(project.getId())
                .lifecycleStatus(project.getLifecycleStatus())
                .reviewStatus(project.getReviewStatus())
                .approvedAt(project.getApprovedAt())
                .rejectedAt(project.getRejectedAt())
                .rejectedReason(project.getRejectedReason())
                .build();
    }
}