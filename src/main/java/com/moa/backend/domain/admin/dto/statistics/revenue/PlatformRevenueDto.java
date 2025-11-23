package com.moa.backend.domain.admin.dto.statistics.revenue;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * 플랫폼 수익 요약
 */
@Getter
@Builder
@AllArgsConstructor
public class PlatformRevenueDto {

    private Long totalPaymentAmount;
    private Long pgFeeAmount;
    private Double pgFeeRate;
    private Long platformFeeAmount;
    private Double platformFeeRate;
    private Long otherCosts;
    // 메이커 지급액(총 결제액 - PG 수수료 - 플랫폼 수수료)
    private Long netPayoutToMaker;
    // 플랫폼 순이익(플랫폼 수수료 - PG 수수료 - 기타 비용)
    private Long netPlatformProfit;
}
