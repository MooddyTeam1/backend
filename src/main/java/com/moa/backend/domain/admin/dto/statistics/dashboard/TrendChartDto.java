package com.moa.backend.domain.admin.dto.statistics.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * 트렌드 차트 DTO
 */
@Getter
@Builder
@AllArgsConstructor
@io.swagger.v3.oas.annotations.media.Schema(description = "트렌드 차트")
public class TrendChartDto {

    @io.swagger.v3.oas.annotations.media.Schema(description = "일별 데이터 리스트")
    private List<TrendDataDto> data;  // 일별 데이터 리스트
}
