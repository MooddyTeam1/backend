package com.moa.backend.domain.admin.dto.statistics.monthly;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * 월별 트렌드 차트 (일별 데이터)
 */
@Getter
@Builder
@AllArgsConstructor
@Schema(description = "월별 트렌드 차트")
public class MonthlyTrendChartDto {

    @Schema(description = "일별 데이터")
    private List<MonthlyTrendDataDto> data;
}
