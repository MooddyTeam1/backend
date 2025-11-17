package com.moa.backend.domain.project.dto.CreateProject;

import com.moa.backend.domain.project.entity.Project;
import com.moa.backend.domain.project.entity.ProjectReviewStatus;
import com.moa.backend.domain.reward.entity.Reward;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

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

    private List<String> rewardNames;

    public static CreateProjectResponse from(Project project) {
        return CreateProjectResponse.builder()
                .projectId(project.getId())
                .maker(project.getMaker().getBusinessName())
                .title(project.getTitle())
                .requestAt(project.getRequestAt())
                .reviewStatus(project.getReviewStatus())
                .rewardNames(project.getRewards().stream().map(Reward::getName).toList())
                .build();
    }
}
