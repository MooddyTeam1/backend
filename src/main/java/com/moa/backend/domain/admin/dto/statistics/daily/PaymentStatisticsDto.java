package com.moa.backend.domain.admin.dto.statistics.daily;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 결제 통계 (성공/실패/환불)
 */
@Getter
@Builder
@AllArgsConstructor
@Schema(description = "결제 통계")
public class PaymentStatisticsDto {

    @Schema(description = "시도 횟수", example = "150")
    private Long attemptCount;
    @Schema(description = "성공 횟수", example = "140")
    private Long successCount;
    @Schema(description = "성공률(%)", example = "93.3")
    private Double successRate;
    @Schema(description = "실패 횟수", example = "10")
    private Long failureCount;
    @Schema(description = "환불 건수", example = "3")
    private Long refundCount;
    @Schema(description = "환불 금액", example = "180000")
    private Long refundAmount;
}
