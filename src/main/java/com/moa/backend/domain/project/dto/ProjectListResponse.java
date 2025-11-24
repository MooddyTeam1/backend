package com.moa.backend.domain.project.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.moa.backend.domain.project.entity.Category;
import com.moa.backend.domain.project.entity.Project;
import com.moa.backend.domain.project.entity.ProjectLifecycleStatus;
import com.moa.backend.domain.project.entity.ProjectResultStatus;
import com.moa.backend.domain.project.entity.ProjectReviewStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

// 한글 설명: 프로젝트 카드(홈/검색/카테고리/마이페이지/트래킹 섹션) 전용 DTO.
// - 상세 페이지용 정보(summary, storyMarkdown, rewards 등)는 포함하지 않는다.
// - JsonInclude.NON_NULL: null 필드는 JSON 응답에서 제외.
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProjectListResponse {

    // ================== 기본 정보(카드 공통) ==================
    private Long id;                 // 프로젝트 ID
    private String maker;            // 메이커 이름
    private String title;            // 프로젝트 제목
    private Category category;       // 카테고리
    private String coverImageUrl;    // 카드 썸네일 이미지 URL

    // ================== 펀딩 금액/달성 정보 ==================

    // 한글 설명: 목표 금액(원). (예: 5_000_000)
    private Long goalAmount;

    // 한글 설명: 현재까지 결제 완료(PAID) 기준 모인 금액 합계(원).
    private Long fundedAmount;

    // 한글 설명: 후원자 수(고유 서포터 수 기준).
    private Long supporterCount;

    // 한글 설명: 달성률(0~100 정수 퍼센트).
    // - fundedAmount / goalAmount * 100 결과를 floor 한 값.
    private Integer achievementRate;

    // ================== 기간/상태 정보 ==================

    // 한글 설명: 펀딩 종료일(카드에서 D-표시 등에 사용).
    private LocalDate endDate;

    // 한글 설명: 라이브 시작/끝 시각 (LIVE 구간 표시용).
    private LocalDateTime liveStartAt;
    private LocalDateTime liveEndAt;

    // 한글 설명: 진행 상태(DRAFT/LIVE/ENDED 등).
    private ProjectLifecycleStatus lifecycleStatus;

    // 한글 설명: 심사 상태(NONE/REVIEW/APPROVED/REJECTED 등).
    private ProjectReviewStatus reviewStatus;

    // 한글 설명: 결과 상태(SUCCESS/FAIL 등, 종료된 프로젝트에서 사용).
    private ProjectResultStatus resultStatus;

    // ================== ❤️ 찜(북마크) 정보 ==================

    // 한글 설명: 이 프로젝트를 찜한 총 개수(서포터 수).
    private Long bookmarkCount;

    // 한글 설명: 현재 로그인한 사용자가 이 프로젝트를 찜했는지 여부.
    // - 비로그인/정보 없음이면 null 로 내려가도 되고, false로 내려가도 무방.
    private Boolean bookmarkedByMe;

    // ================== 🏷 홈/리스트용 뱃지 플래그 ==================

    // 한글 설명: '방금 업로드된 신규 프로젝트' 섹션/뱃지 여부.
    private boolean badgeNew;

    // 한글 설명: '마감 임박' 섹션/뱃지 여부.
    private boolean badgeClosingSoon;

    // 한글 설명: '성공 메이커의 새 프로젝트' 섹션/뱃지 여부.
    private boolean badgeSuccessMaker;

    // 한글 설명: '첫 도전 메이커 응원하기' 섹션/뱃지 여부.
    private boolean badgeFirstChallengeMaker;

    // ================== 📈 트래킹/통계 정보 (옵션) ==================

    // 한글 설명: 특정 기간 기준 상세 페이지 뷰 수 (예: 최근 1시간/3시간/24시간).
    private Long recentViewCount;

    // 한글 설명: 뷰 집계 구간 라벨 (예: "최근 1시간", "최근 3시간").
    private String trafficWindowLabel;

    // 한글 설명: 트렌딩 점수 (뷰/찜/결제 등을 가중합한 값).
    private Double trendingScore;

    // ============================================================
    // 공용 정적 팩토리 메서드들
    // ============================================================

    // 한글 설명:
    //  - 검색/카테고리/홈/마이페이지/트래킹 카드 등에서 공통으로 사용하는 기본 카드 매핑.
    //  - 금액/찜/후원자/트래킹 정보 등은 서비스에서 필요 시 builder로 추가 세팅.
    public static ProjectListResponseBuilder base(Project project) {
        return ProjectListResponse.builder()
                .id(project.getId())
                .maker(project.getMaker().getName())
                .title(project.getTitle())
                .category(project.getCategory())
                .coverImageUrl(project.getCoverImageUrl())
                .goalAmount(project.getGoalAmount())
                .endDate(project.getEndDate())
                .liveStartAt(project.getLiveStartAt())
                .liveEndAt(project.getLiveEndAt())
                .lifecycleStatus(project.getLifecycleStatus())
                .reviewStatus(project.getReviewStatus())
                .resultStatus(project.getResultStatus());
    }

    // 한글 설명:
    //  - 검색/카테고리 조회에서 사용하는 엔트리 포인트.
    //  - 현재 설계 방향상, base(project)와 동일한 카드 형태로 내려준다.
    public static ProjectListResponse searchProjects(Project project) {
        return base(project).build();
    }

    // 한글 설명:
    //  - 홈 섹션에서 뱃지 정보를 함께 내려줄 때 사용하는 헬퍼 메서드.
    //  - 금액/찜/후원자/트래킹 정보는 서비스 레이어에서 builder로 추가 세팅.
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

    // ============================================================
    // 트래킹/통계 섹션 전용 헬퍼
    // ============================================================

    // 한글 설명:
    //  - "지금 많이 보고 있는 프로젝트" 섹션용 카드 생성 헬퍼.
    //  - 기본 카드 정보 + recentViewCount + trafficWindowLabel 만 세팅한다.
    public static ProjectListResponse fromMostViewed(Project project,
                                                     long viewCount,
                                                     String windowLabel) {
        return ProjectListResponse.base(project)
                .recentViewCount(viewCount)
                .trafficWindowLabel(windowLabel)
                .build();
    }

    // 한글 설명:
    //  - "지금 뜨는 프로젝트(트렌딩)" 섹션용 카드 생성 헬퍼.
    //  - paidAmount는 fundedAmount로, bookmarkCount/최근 뷰 수/트렌딩 점수를 함께 세팅한다.
    //  - 달성률(achievementRate)은 서비스에서 계산해서 넘겨준다.
    public static ProjectListResponse fromTrending(Project project,
                                                   long recentViewCount,
                                                   long bookmarkCount,
                                                   long paidAmount,
                                                   double score,
                                                   Integer achievementRate) {
        return ProjectListResponse.base(project)
                .fundedAmount(paidAmount)
                .bookmarkCount(bookmarkCount)
                .recentViewCount(recentViewCount)
                .trendingScore(score)
                .achievementRate(achievementRate)
                .build();
    }
}
