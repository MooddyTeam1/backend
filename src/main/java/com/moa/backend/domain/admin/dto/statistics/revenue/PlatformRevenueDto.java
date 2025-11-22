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
    private Long netProfit;
}
