package com.moa.backend.domain.admin.dto.statistics.daily;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * 시간대별 차트 데이터 (0~23시)
 */
@Getter
@Builder
@AllArgsConstructor
@Schema(description = "시간대별 차트 데이터 (0~23시)")
public class HourlyChartDto {

    @Schema(description = "시간대 데이터 목록")
    private List<HourlyDataDto> data;
}
