package com.moa.backend.domain.admin.dto.statistics.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * KPI 요약 DTO (6개 항목)
 */
@Getter
@Builder
@AllArgsConstructor
public class KpiSummaryDto {

    private KpiItemDto totalFundingAmount;      // 총 펀딩 금액
    private KpiItemDto totalOrderCount;         // 총 결제 건수
    private KpiItemDto platformFeeRevenue;      // 플랫폼 수수료 수익
    private KpiItemDto newProjectCount;         // 신규 프로젝트 수
    private KpiItemDto newUserCount;            // 신규 가입자 수
    private KpiItemDto activeSupporterCount;    // 활성 서포터 수
}
