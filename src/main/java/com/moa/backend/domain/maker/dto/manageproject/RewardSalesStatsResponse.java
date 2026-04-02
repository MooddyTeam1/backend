package com.moa.backend.domain.maker.dto.manageproject;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "RewardSalesStatsResponse DTO")
public class RewardSalesStatsResponse {

    // 한글 설명: 리워드 ID
    @Schema(description = "rewardId", example = "1")
    private Long rewardId;

    // 한글 설명: 리워드명
    @Schema(description = "rewardTitle", example = "rewardTitle")
    private String rewardTitle;

    // 한글 설명: 판매 수량 (주문 수량 합계)
    @Schema(description = "salesCount", example = "1")
    private Integer salesCount;

    // 한글 설명: 판매 금액 합계 (원)
    @Schema(description = "totalAmount", example = "1")
    private Long totalAmount;

    // 한글 설명: 전체 판매 수량 대비 비율 (%), 소수점 1자리
    @Schema(description = "percentage", example = "12.5")
    private Double percentage;
}
