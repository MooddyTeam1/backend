package com.moa.backend.domain.maker.dto.manageproject;

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
public class MakerProjectDetailResponse {

    // ===== 기본 정보 =====

    // 한글 설명: 프로젝트 ID
    private Long id;

    // 한글 설명: 썸네일 이미지 URL
    private String thumbnailUrl;

    // 한글 설명: 프로젝트 제목
    private String title;

    // 한글 설명: 프로젝트 요약 설명
    private String summary;

    // 한글 설명: 카테고리명 (한글)
    private String category;

    // 한글 설명: 프로젝트 상태 문자열
    // 예: DRAFT, REVIEW, APPROVED, SCHEDULED, LIVE, ENDED_SUCCESS, ENDED_FAILED, REJECTED
    private String status;

    // 한글 설명: 목표 모금액 (원)
    private Long goalAmount;

    // 한글 설명: 현재 모금액 (원)
    private Long currentAmount;

    // 한글 설명: 진행률 (%) = (currentAmount / goalAmount) * 100
    private Double progressPercent;

    // 한글 설명: 서포터 수 (중복 제거)
    private Integer supporterCount;

    // 한글 설명: 종료일까지 남은 일수 (null: 이미 종료된 프로젝트)
    private Integer daysLeft;

    // 한글 설명: 프로젝트 시작일시 (ISO 8601)
    private LocalDateTime startDate;

    // 한글 설명: 프로젝트 종료일시 (ISO 8601)
    private LocalDateTime endDate;

    // ===== 통계/그래프 영역 =====

    // 한글 설명: 상단 카드 영역에서 사용하는 요약 통계
    private ProjectDetailStatsResponse stats;

    // 한글 설명: 일별 방문수/후원수/모금액 그래프 데이터
    private List<DailyStatsResponse> dailyStats;

    // 한글 설명: 채널별 유입 통계 (도넛 차트)
    private List<ChannelStatsResponse> channelStats;

    // 한글 설명: 리워드별 판매 통계 (도넛 차트)
    private List<RewardSalesStatsResponse> rewardSalesStats;

    // ===== 리워드/주문/커뮤니케이션 =====

    // 한글 설명: 프로젝트에 등록된 리워드 요약 목록
    private List<RewardSummaryResponse> rewards;

    // 한글 설명: 최근 주문 목록 (최신 10건)
    private List<MakerProjectOrderSummaryResponse> recentOrders;

    // 한글 설명: 프로젝트 공지 목록
    private List<ProjectNoticeResponse> notices;

    // 한글 설명: 프로젝트 Q&A 목록
    private List<ProjectQnaResponse> qnas;

    // ===== 정산 정보 =====

    // 한글 설명: 정산 관련 정보 (예상 정산액, 수수료 등)
    private ProjectSettlementResponse settlement;

    // ===== 메타 정보 =====

    // 한글 설명: 프로젝트 생성일시
    private LocalDateTime createdAt;

    // 한글 설명: 프로젝트 수정일시
    private LocalDateTime updatedAt;

    // 한글 설명: 프로젝트 승인일시
    private LocalDateTime approvedAt;

    // 한글 설명: 반려 사유 (REJECTED인 경우)
    private String rejectedReason;
}
