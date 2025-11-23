package com.moa.backend.domain.admin.dto.statistics.daily;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * 시간대별 차트 데이터 (0~23시)
 */
@Getter
@Builder
@AllArgsConstructor
public class HourlyChartDto {

    private List<HourlyDataDto> data;
}
