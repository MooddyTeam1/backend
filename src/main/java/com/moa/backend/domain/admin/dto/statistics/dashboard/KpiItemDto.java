package com.moa.backend.domain.admin.dto.statistics.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * 개별 KPI 항목 DTO
 * 예: 총 펀딩 금액, 총 결제 건수 등
 */
@Getter
@Builder
@AllArgsConstructor
public class KpiItemDto {

    private Long value;           // 현재 값
    private Double changeRate;    // 증가율 (%)
    private Long changeAmount;    // 증가 금액/건수
}
