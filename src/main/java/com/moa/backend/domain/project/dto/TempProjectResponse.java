package com.moa.backend.domain.project.dto;

import com.moa.backend.domain.project.entity.Category;
import com.moa.backend.domain.project.entity.Project;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TempProjectResponse {
    private Long projectId;
    private String maker;
    private String title;
    private String summary;
    private String storyMarkdown;
    private Long goalAmount;
    private LocalDate startDate;
    private LocalDate endDate;
    private Category category;
    private String coverImageUrl;
    private List<String> coverGallery;
    private List<String> tags;

    public static TempProjectResponse from(Project project) {
        return TempProjectResponse.builder()
                .projectId(project.getId())
                .maker(project.getMaker().getBusinessName())
                .title(project.getTitle())
                .summary(project.getSummary())
                .storyMarkdown(project.getStoryMarkdown())
                .goalAmount(project.getGoalAmount())
                .startDate(project.getStartDate())
                .endDate(project.getEndDate())
                .category(project.getCategory())
                .coverImageUrl(project.getCoverImageUrl())
                .coverGallery(project.getCoverGallery())
                .tags(project.getTags())
                .build();
    }
}
