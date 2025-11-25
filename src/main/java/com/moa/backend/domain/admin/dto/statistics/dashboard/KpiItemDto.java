package com.moa.backend.domain.admin.dto.statistics.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 개별 KPI 항목 DTO
 * 예: 총 펀딩 금액, 총 결제 건수 등
 */
@Getter
@Builder
@AllArgsConstructor
@Schema(description = "KPI 단일 항목")
public class KpiItemDto {

    @Schema(description = "현재 값", example = "125000000")
    private Long value;           // 현재 값
    @Schema(description = "증가율(%)", example = "12.5")
    private Double changeRate;    // 증가율 (%)
    @Schema(description = "증감 금액/건수", example = "15000000")
    private Long changeAmount;    // 증가 금액/건수
}
