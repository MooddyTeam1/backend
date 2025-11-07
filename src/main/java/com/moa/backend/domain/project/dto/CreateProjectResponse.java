package com.moa.backend.domain.project.dto;

import com.moa.backend.domain.project.entity.Project;
import com.moa.backend.domain.project.entity.ProjectReviewStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateProjectResponse {
    private Long projectId;
    private String maker;
    private String title;
    private LocalDateTime requestAt;
    private ProjectReviewStatus reviewStatus;

    public static CreateProjectResponse from(Project project) {
        return CreateProjectResponse.builder()
                .projectId(project.getId())
                .maker(project.getMaker().getBusinessName())
                .title(project.getTitle())
                .requestAt(project.getRequestAt())
                .reviewStatus(project.getReviewStatus())
                .build();
    }
}
