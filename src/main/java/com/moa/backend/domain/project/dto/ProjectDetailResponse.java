package com.moa.backend.domain.project.dto;

import com.moa.backend.domain.project.entity.*;
import com.moa.backend.domain.reward.dto.RewardResponse;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectDetailResponse {
    private Long id;
    private String maker;
    private String title;
    private String summary;
    private String storyMarkdown;
    private Long goalAmount;
    private LocalDate startDate;
    private LocalDate endDate;
    private Category category;
    private ProjectLifecycleStatus lifecycleStatus;
    private ProjectReviewStatus reviewStatus;
    private ProjectResultStatus resultStatus;
    private String coverImageUrl;
    private List<String> coverGallery;
    private List<String> tags;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime requestAt;
    private LocalDateTime approvedAt;
    private LocalDateTime rejectedAt;
    private String rejectedReason;
    private LocalDateTime liveStartAt;
    private LocalDateTime liveEndAt;

    private List<RewardResponse> rewards;

    public static ProjectDetailResponse from(Project project) {
        return ProjectDetailResponse.builder()
                .id(project.getId())
                .maker(project.getMaker().getBusinessName())
                .title(project.getTitle())
                .summary(project.getSummary())
                .storyMarkdown(project.getStoryMarkdown())
                .goalAmount(project.getGoalAmount())
                .startDate(project.getStartDate())
                .endDate(project.getEndDate())
                .category(project.getCategory())
                .lifecycleStatus(project.getLifecycleStatus())
                .reviewStatus(project.getReviewStatus())
                .resultStatus(project.getResultStatus())
                .coverImageUrl(project.getCoverImageUrl())
                .coverGallery(project.getCoverGallery())
                .tags(project.getTags())
                .createdAt(project.getCreatedAt())
                .updatedAt(project.getUpdatedAt())
                .requestAt(project.getRequestAt())
                .approvedAt(project.getApprovedAt())
                .rejectedAt(project.getRejectedAt())
                .rejectedReason(project.getRejectedReason())
                .liveStartAt(project.getLiveStartAt())
                .liveEndAt(project.getLiveEndAt())
                .rewards(project.getRewards().stream()
                        .map(RewardResponse::from).toList())
                .build();
    }
}
