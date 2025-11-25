package com.moa.backend.domain.maker.dto.manageproject;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 한글 설명: 프로젝트 상세 상단 요약 통계 DTO.
 * - todayViews, totalViews, totalRaised, supporterCount 등 핵심 통계를 담는다.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectDetailStatsResponse {

    // 한글 설명: 오늘 방문수 (당일 00:00 ~ 현재)
    private Integer todayViews;

    // 한글 설명: 전체 누적 방문수
    private Long totalViews;

    // 한글 설명: 전체 모금액 (원)
    private Long totalRaised;

    // 한글 설명: 목표 금액 (원)
    private Long goalAmount;

    // 한글 설명: 달성률 (%) = totalRaised / goalAmount * 100
    private Double progressPercent;

    // 한글 설명: 서포터 수 (중복 제거)
    private Integer supporterCount;

    // 한글 설명: 재후원자 비율 (%) = (재후원 서포터 수 / 전체 서포터 수) * 100
    private Double repeatSupporterRate;

    // 한글 설명: 평균 후원 금액 (원) = totalRaised / supporterCount
    private Long averageSupportAmount;

    // 한글 설명: 가장 많이 선택된 리워드 정보
    private TopRewardResponse topReward;

    /**
     * 한글 설명: 가장 많이 선택된 리워드 정보 DTO.
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TopRewardResponse {

        // 한글 설명: 리워드 ID
        private Long id;

        // 한글 설명: 리워드명
        private String title;

        // 한글 설명: 선택된 횟수 (주문 수량 합계)
        private Integer count;
    }
}
