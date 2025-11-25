package com.moa.backend.domain.settlement.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "정산 요약(상태별 건수/금액)")
public class SettlementSummaryResponse {

    @Schema(description = "PENDING 건수", example = "2")
    private final long pendingCount;
    @Schema(description = "PENDING 금액 합계", example = "500000")
    private final long pendingAmount;

    @Schema(description = "FIRST_PAID 건수", example = "1")
    private final long firstPaidCount;
    @Schema(description = "FIRST_PAID 금액 합계", example = "150000")
    private final long firstPaidAmount;

    @Schema(description = "FINAL_READY 건수", example = "0")
    private final long finalReadyCount;
    @Schema(description = "FINAL_READY 금액 합계", example = "0")
    private final long finalReadyAmount;

    @Schema(description = "COMPLETED 건수", example = "3")
    private final long completedCount;
    @Schema(description = "COMPLETED 금액 합계", example = "1200000")
    private final long completedAmount;
}
