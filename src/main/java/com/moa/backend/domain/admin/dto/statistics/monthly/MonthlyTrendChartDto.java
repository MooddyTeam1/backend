package com.moa.backend.domain.admin.dto.statistics.monthly;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * 월별 트렌드 차트 (일별 데이터)
 */
@Getter
@Builder
@AllArgsConstructor
public class MonthlyTrendChartDto {

    private List<MonthlyTrendDataDto> data;
}
