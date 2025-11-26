package com.moa.backend.domain.project.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.moa.backend.domain.project.entity.Category;
import com.moa.backend.domain.project.entity.Project;
import com.moa.backend.domain.project.entity.ProjectLifecycleStatus;
import com.moa.backend.domain.project.entity.ProjectResultStatus;
import com.moa.backend.domain.project.entity.ProjectReviewStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Lightweight project card response used across the public sections
 * (search, category lists, maker pages, and home widgets).
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Project card/list response")
public class ProjectListResponse {

    // Basic identity & visuals
    private Long id;
    private String maker;
    private String summary;
    private String title;
    private Category category;
    private String coverImageUrl;
    private List<String> coverGallery;

    // Funding snapshot
    private Long goalAmount;

    @JsonProperty("raised")
    private Long fundedAmount;

    @JsonProperty("backerCount")
    private Long supporterCount;

    private Integer achievementRate;

    // Schedule & lifecycle
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDateTime liveStartAt;
    private LocalDateTime liveEndAt;
    private ProjectLifecycleStatus lifecycleStatus;
    private ProjectReviewStatus reviewStatus;
    private ProjectResultStatus resultStatus;
    private Boolean live;
    private Boolean scheduled;
    private Long daysLeft;

    // Bookmark info
    private Long bookmarkCount;
    private Boolean bookmarkedByMe;

    // Section badges
    private boolean badgeNew;
    private boolean badgeClosingSoon;
    private boolean badgeSuccessMaker;
    private boolean badgeFirstChallengeMaker;

    // Traffic / trending metrics
    @JsonProperty("viewCount")
    private Long recentViewCount;

    @JsonProperty("windowLabel")
    private String trafficWindowLabel;

    @JsonProperty("score")
    private Double trendingScore;

    public static ProjectListResponseBuilder base(Project project) {
        boolean isLive = project.getLifecycleStatus() == ProjectLifecycleStatus.LIVE;
        boolean isScheduled = project.getLifecycleStatus() == ProjectLifecycleStatus.SCHEDULED;
        Long remainingDays = null;
        if (project.getEndDate() != null) {
            long diff = ChronoUnit.DAYS.between(LocalDate.now(), project.getEndDate());
            remainingDays = Math.max(diff, 0);
        }

        return ProjectListResponse.builder()
                .id(project.getId())
                .maker(project.getMaker().getName())
                .summary(project.getSummary())
                .title(project.getTitle())
                .category(project.getCategory())
                .coverImageUrl(project.getCoverImageUrl())
                .coverGallery(copyGallery(project.getCoverGallery()))
                .goalAmount(project.getGoalAmount())
                .startDate(project.getStartDate())
                .endDate(project.getEndDate())
                .liveStartAt(project.getLiveStartAt())
                .liveEndAt(project.getLiveEndAt())
                .lifecycleStatus(project.getLifecycleStatus())
                .reviewStatus(project.getReviewStatus())
                .resultStatus(project.getResultStatus())
                .live(isLive)
                .scheduled(isScheduled)
                .daysLeft(remainingDays);
    }

    public static ProjectListResponse searchProjects(Project project) {
        return base(project).build();
    }

    public static ProjectListResponse fromWithBadges(
            Project project,
            boolean badgeNew,
            boolean badgeClosingSoon,
            boolean badgeSuccessMaker,
            boolean badgeFirstChallengeMaker
    ) {
        return ProjectListResponse.base(project)
                .badgeNew(badgeNew)
                .badgeClosingSoon(badgeClosingSoon)
                .badgeSuccessMaker(badgeSuccessMaker)
                .badgeFirstChallengeMaker(badgeFirstChallengeMaker)
                .build();
    }

    public static ProjectListResponse fromMostViewed(Project project,
                                                     long viewCount,
                                                     String windowLabel) {
        return ProjectListResponse.base(project)
                .recentViewCount(viewCount)
                .trafficWindowLabel(windowLabel)
                .build();
    }

    /**
     * Most-viewed 카드 + 모금/후원 지표까지 포함하는 빌더.
     * - raised(fundedAmount), backerCount(supporterCount), achievementRate 세팅
     * - recentViewCount, trafficWindowLabel 함께 제공
     */
    public static ProjectListResponse fromMostViewedWithFunding(Project project,
                                                               long viewCount,
                                                               String windowLabel,
                                                               long paidAmount,
                                                               long supporterCount,
                                                               Integer achievementRate) {
        return ProjectListResponse.base(project)
                .fundedAmount(paidAmount)
                .supporterCount(supporterCount)
                .achievementRate(achievementRate)
                .recentViewCount(viewCount)
                .trafficWindowLabel(windowLabel)
                .build();
    }

    public static ProjectListResponse fromTrending(Project project,
                                                   long recentViewCount,
                                                   long bookmarkCount,
                                                   long paidAmount,
                                                   long supporterCount,
                                                   double score,
                                                   Integer achievementRate) {
        return ProjectListResponse.base(project)
                .fundedAmount(paidAmount)
                .supporterCount(supporterCount)
                .bookmarkCount(bookmarkCount)
                .recentViewCount(recentViewCount)
                .trendingScore(score)
                .achievementRate(achievementRate)
                .build();
    }

    @JsonProperty("makerName")
    public String makerNameAlias() {
        return maker;
    }

    private static List<String> copyGallery(List<String> gallery) {
        if (gallery == null || gallery.isEmpty()) {
            return null;
        }
        return List.copyOf(gallery);
    }
}
