package com.moa.backend.domain.maker.dto.manageproject;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 한글 설명: 메이커 프로젝트 상세 관리 페이지 전체 응답 DTO.
 * - 명세서의 /api/maker/projects/{projectId} 응답 JSON 구조를 그대로 반영한다.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "MakerProjectDetailResponse DTO")
public class MakerProjectDetailResponse {

    // ===== 기본 정보 =====

    // 한글 설명: 프로젝트 ID
    @Schema(description = "id", example = "1")
    private Long id;

    // 한글 설명: 썸네일 이미지 URL
    @Schema(description = "thumbnailUrl", example = "thumbnailUrl")
    private String thumbnailUrl;

    // 한글 설명: 프로젝트 제목
    @Schema(description = "title", example = "title")
    private String title;

    // 한글 설명: 프로젝트 요약 설명
    @Schema(description = "summary", example = "summary")
    private String summary;

    // 한글 설명: 카테고리명 (한글)
    @Schema(description = "category", example = "category")
    private String category;

    // 한글 설명: 프로젝트 상태 문자열
    // 예: DRAFT, REVIEW, APPROVED, SCHEDULED, LIVE, ENDED_SUCCESS, ENDED_FAILED, REJECTED
    @Schema(description = "status", example = "status")
    private String status;

    // 한글 설명: 목표 모금액 (원)
    @Schema(description = "goalAmount", example = "1")
    private Long goalAmount;

    // 한글 설명: 현재 모금액 (원)
    @Schema(description = "currentAmount", example = "1")
    private Long currentAmount;

    // 한글 설명: 진행률 (%) = (currentAmount / goalAmount) * 100
    @Schema(description = "progressPercent", example = "12.5")
    private Double progressPercent;

    // 한글 설명: 서포터 수 (중복 제거)
    @Schema(description = "supporterCount", example = "1")
    private Integer supporterCount;

    // 한글 설명: 종료일까지 남은 일수 (null: 이미 종료된 프로젝트)
    @Schema(description = "daysLeft", example = "1")
    private Integer daysLeft;

    // 한글 설명: 프로젝트 시작일시 (ISO 8601)
    @Schema(description = "startDate", example = "2025-11-01T10:00:00")
    private LocalDateTime startDate;

    // 한글 설명: 프로젝트 종료일시 (ISO 8601)
    @Schema(description = "endDate", example = "2025-11-01T10:00:00")
    private LocalDateTime endDate;

    // ===== 통계/그래프 영역 =====

    // 한글 설명: 상단 카드 영역에서 사용하는 요약 통계
    @Schema(description = "stats", example = "stats")
    private ProjectDetailStatsResponse stats;

    // 한글 설명: 일별 방문수/후원수/모금액 그래프 데이터
    @Schema(description = "dailyStats", example = "[]")
    private List<DailyStatsResponse> dailyStats;

    // 한글 설명: 채널별 유입 통계 (도넛 차트)
    @Schema(description = "channelStats", example = "[]")
    private List<ChannelStatsResponse> channelStats;

    // 한글 설명: 리워드별 판매 통계 (도넛 차트)
    @Schema(description = "rewardSalesStats", example = "[]")
    private List<RewardSalesStatsResponse> rewardSalesStats;

    // ===== 리워드/주문/커뮤니케이션 =====

    // 한글 설명: 프로젝트에 등록된 리워드 요약 목록
    @Schema(description = "rewards", example = "[]")
    private List<RewardSummaryResponse> rewards;

    // 한글 설명: 최근 주문 목록 (최신 10건)
    @Schema(description = "recentOrders", example = "[]")
    private List<MakerProjectOrderSummaryResponse> recentOrders;

    // 한글 설명: 프로젝트 공지 목록
    @Schema(description = "notices", example = "[]")
    private List<ProjectNoticeResponse> notices;

    // 한글 설명: 프로젝트 Q&A 목록
    @Schema(description = "qnas", example = "[]")
    private List<ProjectQnaResponse> qnas;

    // ===== 정산 정보 =====

    // 한글 설명: 정산 관련 정보 (예상 정산액, 수수료 등)
    @Schema(description = "settlement", example = "settlement")
    private ProjectSettlementResponse settlement;

    // ===== 메타 정보 =====

    // 한글 설명: 프로젝트 생성일시
    @Schema(description = "createdAt", example = "2025-11-01T10:00:00")
    private LocalDateTime createdAt;

    // 한글 설명: 프로젝트 수정일시
    @Schema(description = "updatedAt", example = "2025-11-01T10:00:00")
    private LocalDateTime updatedAt;

    // 한글 설명: 프로젝트 승인일시
    @Schema(description = "approvedAt", example = "2025-11-01T10:00:00")
    private LocalDateTime approvedAt;

    // 한글 설명: 반려 사유 (REJECTED인 경우)
    @Schema(description = "rejectedReason", example = "rejectedReason")
    private String rejectedReason;
}
