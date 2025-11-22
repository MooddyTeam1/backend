package com.moa.backend.domain.admin.dto.statistics.daily;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * 결제 통계 (성공/실패/환불)
 */
@Getter
@Builder
@AllArgsConstructor
public class PaymentStatisticsDto {

    private Long attemptCount;
    private Long successCount;
    private Double successRate;
    private Long failureCount;
    private Long refundCount;
    private Long refundAmount;
}
