package com.moa.backend.domain.admin.dto.statistics.revenue;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * 메이커 정산 요약
 */
@Getter
@Builder
@AllArgsConstructor
public class MakerSettlementSummaryDto {

    private Long totalSettlementAmount;
    private Long pendingAmount;
    private Long processingAmount;
    private Long completedAmount;
}
