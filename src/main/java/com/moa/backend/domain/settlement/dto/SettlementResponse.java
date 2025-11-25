package com.moa.backend.domain.settlement.dto;

import com.moa.backend.domain.settlement.entity.Settlement;
import com.moa.backend.domain.settlement.entity.SettlementPayoutStatus;
import com.moa.backend.domain.settlement.entity.SettlementStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@Schema(description = "정산 응답")
public class SettlementResponse {

    @Schema(description = "정산 ID", example = "7001")
    private final Long settlementId;
    @Schema(description = "프로젝트 ID", example = "101")
    private final Long projectId;
    @Schema(description = "메이커 ID", example = "10")
    private final Long makerId;
    @Schema(description = "총 주문 금액", example = "10000000")
    private final Long totalOrderAmount;
    @Schema(description = "PG 수수료", example = "300000")
    private final Long tossFeeAmount;
    @Schema(description = "플랫폼 수수료", example = "700000")
    private final Long platformFeeAmount;
    @Schema(description = "정산 대상 금액(순액)", example = "9000000")
    private final Long netAmount;
    @Schema(description = "1차 지급 금액", example = "6000000")
    private final Long firstPaymentAmount;
    @Schema(description = "최종 지급 금액", example = "3000000")
    private final Long finalPaymentAmount;
    @Schema(description = "정산 상태", example = "COMPLETED")
    private final SettlementStatus status;
    @Schema(description = "1차 지급 상태", example = "PAID")
    private final SettlementPayoutStatus firstPaymentStatus;
    @Schema(description = "최종 지급 상태", example = "PENDING")
    private final SettlementPayoutStatus finalPaymentStatus;
    @Schema(description = "1차 지급 시각", example = "2025-01-20T12:00:00")
    private final LocalDateTime firstPaymentAt;
    @Schema(description = "최종 지급 시각", example = "2025-02-01T12:00:00")
    private final LocalDateTime finalPaymentAt;
    @Schema(description = "지급 재시도 횟수", example = "0")
    private final Integer retryCount;

    public static SettlementResponse from(Settlement settlement) {
        return SettlementResponse.builder()
                .settlementId(settlement.getId())
                .projectId(settlement.getProject().getId())
                .makerId(settlement.getMaker().getId())
                .totalOrderAmount(settlement.getTotalOrderAmount())
                .tossFeeAmount(settlement.getTossFeeAmount())
                .platformFeeAmount(settlement.getPlatformFeeAmount())
                .netAmount(settlement.getNetAmount())
                .firstPaymentAmount(settlement.getFirstPaymentAmount())
                .finalPaymentAmount(settlement.getFinalPaymentAmount())
                .status(settlement.getStatus())
                .firstPaymentStatus(settlement.getFirstPaymentStatus())
                .finalPaymentStatus(settlement.getFinalPaymentStatus())
                .firstPaymentAt(settlement.getFirstPaymentAt())
                .finalPaymentAt(settlement.getFinalPaymentAt())
                .retryCount(settlement.getRetryCount())
                .build();
    }
}
