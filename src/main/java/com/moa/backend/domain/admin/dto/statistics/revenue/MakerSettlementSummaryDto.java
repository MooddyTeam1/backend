package com.moa.backend.domain.admin.dto.statistics.revenue;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 메이커 정산 요약
 */
@Getter
@Builder
@AllArgsConstructor
@Schema(description = "메이커 정산 요약")
public class MakerSettlementSummaryDto {

    @Schema(description = "정산 대상 총액", example = "9000000")
    private Long totalSettlementAmount;
    @Schema(description = "대기 금액", example = "4000000")
    private Long pendingAmount;
    @Schema(description = "처리 중 금액", example = "3000000")
    private Long processingAmount;
    @Schema(description = "완료 금액", example = "2000000")
    private Long completedAmount;
}
