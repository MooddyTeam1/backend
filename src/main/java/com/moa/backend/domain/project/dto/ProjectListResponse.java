package com.moa.backend.domain.project.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.moa.backend.domain.project.entity.Category;
import com.moa.backend.domain.project.entity.Project;
import com.moa.backend.domain.project.entity.ProjectResultStatus;
import com.moa.backend.domain.reward.dto.RewardResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

// 한글 설명: 프로젝트 목록/카드 형태 응답 DTO.
// - 메이커 마이페이지, 홈 섹션 등에서 재사용한다.
// - JsonInclude.NON_NULL: null 필드는 JSON 응답에서 제외.
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProjectListResponse {

    // ================== 기본 프로젝트 정보 ==================
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
    private LocalDateTime canceledAt;

    private List<RewardResponse> rewards;

    // ================== 🏷 홈/리스트용 뱃지 플래그 ==================

    // 한글 설명: '방금 업로드된 신규 프로젝트' 섹션/뱃지 여부.
    private boolean badgeNew;

    // 한글 설명: '마감 임박' 섹션/뱃지 여부.
    private boolean badgeClosingSoon;

    // 한글 설명: '성공 메이커의 새 프로젝트' 섹션/뱃지 여부.
    private boolean badgeSuccessMaker;

    // 한글 설명: '첫 도전 메이커 응원하기' 섹션/뱃지 여부.
    private boolean badgeFirstChallengeMaker;

    // ✅ 한글 설명: '목표 달성에 가까운 프로젝트' 섹션용 달성률(0~100 정수 퍼센트).
    // - 다른 응답에서는 굳이 안 쓰면 null로 내려가고, JsonInclude.NON_NULL 때문에 JSON에서 제거된다.
    private Integer achievementRate;

    // ============================================================
    // 공용 정적 팩토리 메서드들
    // ============================================================

    // 한글 설명: 검색/카테고리/홈 카드 등에서 사용하는 최소 정보 매핑용.
    public static ProjectListResponse searchProjects(Project project) {
        return ProjectListResponse.builder()
                .id(project.getId())
                .title(project.getTitle())
                .summary(project.getSummary())
                .coverImageUrl(project.getCoverImageUrl())
                .category(project.getCategory())
                .endDate(project.getEndDate())
                .build();
    }

    // 한글 설명: 상태별 상세 카드 응답 - 작성중.
    public static ProjectListResponse fromDraft(Project project) {
        return base(project)
                .storyMarkdown(project.getStoryMarkdown())
                .tags(project.getTags())
                .build();
    }

    // 한글 설명: 상태별 상세 카드 응답 - 심사중.
    public static ProjectListResponse fromReview(Project project) {
        return base(project)
                .requestAt(project.getRequestAt())
                .build();
    }

    // 한글 설명: 상태별 상세 카드 응답 - 승인됨(미분류).
    public static ProjectListResponse fromApproved(Project project) {
        return base(project)
                .approvedAt(project.getApprovedAt())
                .build();
    }

    // 한글 설명: 상태별 상세 카드 응답 - 공개 예정(SCHEDULED).
    public static ProjectListResponse fromScheduled(Project project) {
        return base(project)
                .startDate(project.getStartDate())
                .endDate(project.getEndDate())
                .build();
    }

    // 한글 설명: 상태별 상세 카드 응답 - 진행중(LIVE).
    public static ProjectListResponse fromLive(Project project) {
        return base(project)
                .liveStartAt(project.getLiveStartAt())
                .liveEndAt(project.getLiveEndAt())
                .build();
    }

    // 한글 설명: 상태별 상세 카드 응답 - 종료(ENDED).
    public static ProjectListResponse fromEnded(Project project) {
        return base(project)
                .liveStartAt(project.getLiveStartAt())
                .liveEndAt(project.getLiveEndAt())
                .resultStatus(project.getResultStatus())
                .build();
    }

    // 한글 설명: 상태별 상세 카드 응답 - 반려됨(REJECTED).
    public static ProjectListResponse fromRejected(Project project) {
        return base(project)
                .rejectedReason(project.getRejectedReason())
                .rejectedAt(project.getRejectedAt())
                .build();
    }

    // 한글 설명: 상태별 상세 카드 응답 - 취소됨(CANCELED).
    public static ProjectListResponse fromCanceled(Project project) {
        return base(project)
                .canceledAt(project.getCanceledAt())
                .build();
    }

    // 한글 설명:
    //  - 홈 섹션에서 뱃지 정보를 함께 내려줄 때 사용하는 헬퍼 메서드.
    //  - 기존 base(...) 빌더를 재사용하고, 뱃지 플래그만 추가로 세팅한다.
    public static ProjectListResponse fromWithBadges(
            Project project,
            boolean badgeNew,
            boolean badgeClosingSoon,
            boolean badgeSuccessMaker,
            boolean badgeFirstChallengeMaker
    ) {
        return base(project)
                .badgeNew(badgeNew)
                .badgeClosingSoon(badgeClosingSoon)
                .badgeSuccessMaker(badgeSuccessMaker)
                .badgeFirstChallengeMaker(badgeFirstChallengeMaker)
                .build();
    }

    // 한글 설명:
    //  - 상태별 fromXXX(...)에서 공통으로 사용하는 기본 빌더.
    //  - 메이커명, 금액, 날짜, 썸네일, 리워드 목록 등 공통 필드를 채운다.
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
