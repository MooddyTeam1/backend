package com.moa.backend.domain.admin.dto.statistics.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 일별 트렌드 데이터 DTO
 */
@Getter
@Builder
@AllArgsConstructor
@Schema(description = "일별 트렌드 데이터")
public class TrendDataDto {

    @Schema(description = "날짜(MM/dd)", example = "11/25")
    private String date;            // 날짜 (MM/dd 형식)
    @Schema(description = "펀딩 금액", example = "250000")
    private Long fundingAmount;     // 펀딩 금액
    @Schema(description = "프로젝트 수", example = "5")
    private Integer projectCount;   // 프로젝트 수
    @Schema(description = "주문 건수", example = "42")
    private Integer orderCount;     // 주문 건수
}
