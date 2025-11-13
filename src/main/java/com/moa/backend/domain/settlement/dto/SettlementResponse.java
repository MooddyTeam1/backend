package com.moa.backend.domain.settlement.dto;

import com.moa.backend.domain.settlement.entity.Settlement;
import com.moa.backend.domain.settlement.entity.SettlementPayoutStatus;
import com.moa.backend.domain.settlement.entity.SettlementStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class SettlementResponse {

    private final Long settlementId;
    private final Long projectId;
    private final Long makerId;
    private final Long totalOrderAmount;
    private final Long tossFeeAmount;
    private final Long platformFeeAmount;
    private final Long netAmount;
    private final Long firstPaymentAmount;
    private final Long finalPaymentAmount;
    private final SettlementStatus status;
    private final SettlementPayoutStatus firstPaymentStatus;
    private final SettlementPayoutStatus finalPaymentStatus;
    private final LocalDateTime firstPaymentAt;
    private final LocalDateTime finalPaymentAt;
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
