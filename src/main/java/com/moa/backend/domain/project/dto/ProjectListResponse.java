package com.moa.backend.domain.project.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.moa.backend.domain.project.entity.*;
import com.moa.backend.domain.reward.dto.RewardResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProjectListResponse {
    private Long id;
    private String maker;
    private String title;
    private String summary;
    private String storyMarkdown;
    private LocalDate startDate;
    private Long goalAmount;
    private LocalDate endDate;
    private Category category;
    private String coverImageUrl;
    private List<String> coverGallery;
    private List<String> tags;
    private ProjectResultStatus resultStatus;
    private LocalDateTime requestAt;
    private LocalDateTime approvedAt;
    private LocalDateTime rejectedAt;
    private String rejectedReason;
    private LocalDateTime liveStartAt;
    private LocalDateTime liveEndAt;

    private List<RewardResponse> rewards;

    public static ProjectListResponse searchProjects(Project project) {
        return  ProjectListResponse.builder()
                .id(project.getId())
                .title(project.getTitle())
                .summary(project.getSummary())
                .coverImageUrl(project.getCoverImageUrl())
                .category(project.getCategory())
                .build();
    }

    public static ProjectListResponse fromDraft(Project project) {
        return base(project)
                .storyMarkdown(project.getStoryMarkdown())
                .tags(project.getTags())
                .build();
    }

    public static ProjectListResponse fromReview(Project project) {
        return base(project)
                .requestAt(project.getRequestAt())
                .build();
    }

    public static ProjectListResponse fromApproved(Project project) {
        return base(project)
                .approvedAt(project.getApprovedAt())
                .build();
    }

    public static ProjectListResponse fromScheduled(Project project) {
        return base(project)
                .startDate(project.getStartDate())
                .endDate(project.getEndDate())
                .build();
    }

    public static ProjectListResponse fromLive(Project project) {
        return base(project)
                .liveStartAt(project.getLiveStartAt())
                .liveEndAt(project.getLiveEndAt())
                .build();
    }

    public static ProjectListResponse fromEnded(Project project) {
        return base(project)
                .liveStartAt(project.getLiveStartAt())
                .liveEndAt(project.getLiveEndAt())
                .resultStatus(project.getResultStatus())
                .build();
    }

    public static ProjectListResponse fromRejected(Project project) {
        return base(project)
                .rejectedReason(project.getRejectedReason())
                .rejectedAt(project.getRejectedAt())
                .build();
    }

    public static ProjectListResponseBuilder base(Project project) {
        return ProjectListResponse.builder()
                .id(project.getId())
                .maker(project.getMaker().getName())
                .title(project.getTitle())
                .summary(project.getSummary())
                .goalAmount(project.getGoalAmount())
                .startDate(project.getStartDate())
                .endDate(project.getEndDate())
                .category(project.getCategory())
                .coverImageUrl(project.getCoverImageUrl())
                .coverGallery(project.getCoverGallery())
                .rewards(project.getRewards().stream().map(RewardResponse::from).toList());
    }
}