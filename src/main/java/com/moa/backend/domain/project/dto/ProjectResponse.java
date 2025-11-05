package com.moa.backend.domain.project.dto;

import com.moa.backend.domain.project.entity.Category;
import com.moa.backend.domain.project.entity.Project;
import com.moa.backend.domain.project.entity.ProjectStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectResponse {
    private Long id;
    private String title;
    private String content;
    private Long goalAmount;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private Category category;
    private ProjectStatus status;
    private String thumbnailUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ProjectResponse from(Project project) {
        return ProjectResponse.builder()
                .id(project.getId())
                .title(project.getTitle())
                .content(project.getContent())
                .goalAmount(project.getGoalAmount())
                .startAt(project.getStartAt())
                .endAt(project.getEndAt())
                .category(project.getCategory())
                .status(project.getStatus())
                .thumbnailUrl(project.getThumbnailUrl())
                .createdAt(project.getCreatedAt())
                .updatedAt(project.getUpdatedAt())
                .build();
    }
}