package com.moa.backend.domain.project.dto;

import com.moa.backend.domain.project.entity.Project;
import com.moa.backend.domain.project.entity.ProjectStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProjectStatusResponse {
    private Long id;
    private ProjectStatus status;
    private LocalDateTime approvedAt;
    private LocalDateTime rejectedAt;
    private String rejectionReason;

    public static ProjectStatusResponse from(Project project) {
        return ProjectStatusResponse.builder()
                .id(project.getId())
                .status(project.getStatus())
                .approvedAt(project.getApprovedAt())
                .rejectedAt(project.getRejectedAt())
                .rejectionReason(project.getRejectionReason())
                .build();
    }
}