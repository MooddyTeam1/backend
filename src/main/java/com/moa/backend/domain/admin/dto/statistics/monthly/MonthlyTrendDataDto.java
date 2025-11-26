package com.moa.backend.domain.admin.dto.statistics.monthly;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 월별 일자별 트렌드 데이터
 */
@Getter
@Builder
@AllArgsConstructor
@Schema(description = "월별 일자별 트렌드 데이터")
public class MonthlyTrendDataDto {

    @Schema(description = "날짜(MM/dd)", example = "11/15")
    private String date;          // MM/dd
    @Schema(description = "펀딩 금액", example = "250000")
    private Long fundingAmount;
    @Schema(description = "프로젝트 수", example = "5")
    private Integer projectCount;
    @Schema(description = "주문 건수", example = "42")
    private Integer orderCount;
}
