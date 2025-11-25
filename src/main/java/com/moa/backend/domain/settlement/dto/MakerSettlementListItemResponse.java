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
@Schema(description = "정산 목록 항목(MAKER)")
public class MakerSettlementListItemResponse {

    @Schema(description = "정산 ID", example = "1601")
    private final Long settlementId;
    @Schema(description = "프로젝트 ID", example = "1201")
    private final Long projectId;
    @Schema(description = "프로젝트명", example = "펄스핏 모듈 밴드")
    private final String projectTitle;

    @Schema(description = "정산 상태", example = "FIRST_PAID")
    private final SettlementStatus status;
    @Schema(description = "선지급 상태", example = "DONE")
    private final SettlementPayoutStatus firstPaymentStatus;
    @Schema(description = "잔금 상태", example = "PENDING")
    private final SettlementPayoutStatus finalPaymentStatus;

    @Schema(description = "총 주문 금액", example = "450000")
    private final Long totalOrderAmount;
    @Schema(description = "순액(net)", example = "382500")
    private final Long netAmount;
    @Schema(description = "선지급 금액", example = "150000")
    private final Long firstPaymentAmount;
    @Schema(description = "잔금 금액", example = "232500")
    private final Long finalPaymentAmount;

    @Schema(description = "생성 시각", example = "2025-11-02T11:35:00")
    private final LocalDateTime createdAt;
    @Schema(description = "수정 시각", example = "2025-11-08T10:00:00")
    private final LocalDateTime updatedAt;

    public static MakerSettlementListItemResponse from(Settlement settlement) {
        return MakerSettlementListItemResponse.builder()
                .settlementId(settlement.getId())
                .projectId(settlement.getProject().getId())
                .projectTitle(settlement.getProject().getTitle())
                .status(settlement.getStatus())
                .firstPaymentStatus(settlement.getFirstPaymentStatus())
                .finalPaymentStatus(settlement.getFinalPaymentStatus())
                .totalOrderAmount(settlement.getTotalOrderAmount())
                .netAmount(settlement.getNetAmount())
                .firstPaymentAmount(settlement.getFirstPaymentAmount())
                .finalPaymentAmount(settlement.getFinalPaymentAmount())
                .createdAt(settlement.getCreatedAt())
                .updatedAt(settlement.getUpdatedAt())
                .build();
    }
}
