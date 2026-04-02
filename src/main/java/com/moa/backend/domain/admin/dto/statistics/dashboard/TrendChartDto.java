package com.moa.backend.domain.admin.dto.statistics.dashboard;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "트렌드 차트")
public class TrendChartDto {

    @Schema(description = "시계열 데이터 목록")
    private List<TrendDataDto> data;
}

