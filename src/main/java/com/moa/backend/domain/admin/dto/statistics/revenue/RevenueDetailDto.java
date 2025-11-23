package com.moa.backend.domain.admin.dto.statistics.revenue;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * 수익 리포트 상세 행 (일자/프로젝트 단위)
 */
@Getter
@Builder
@AllArgsConstructor
public class RevenueDetailDto {

    private String date;             // yyyy-MM-dd
    private Long projectId;
    private String projectName;
    private String makerName;
    private Long paymentAmount;
    private Long pgFee;
    private Long platformFee;
    private Long makerSettlementAmount;
    private String settlementStatus;
}
