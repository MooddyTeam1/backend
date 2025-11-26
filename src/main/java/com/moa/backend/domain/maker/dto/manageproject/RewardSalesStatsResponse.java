package com.moa.backend.domain.maker.dto.manageproject;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 한글 설명: 리워드별 판매 통계 DTO.
 * - 리워드별 판매 수량/금액/비율을 담는다.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RewardSalesStatsResponse {

    // 한글 설명: 리워드 ID
    private Long rewardId;

    // 한글 설명: 리워드명
    private String rewardTitle;

    // 한글 설명: 판매 수량 (주문 수량 합계)
    private Integer salesCount;

    // 한글 설명: 판매 금액 합계 (원)
    private Long totalAmount;

    // 한글 설명: 전체 판매 수량 대비 비율 (%), 소수점 1자리
    private Double percentage;
}
