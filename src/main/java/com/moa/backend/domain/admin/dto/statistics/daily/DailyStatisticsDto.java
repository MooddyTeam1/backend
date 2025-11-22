package com.moa.backend.domain.admin.dto.statistics.daily;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * 일일 통계 최상위 DTO
 */
@Getter
@Builder
@AllArgsConstructor
public class DailyStatisticsDto {

    private TrafficDto traffic;
    private ProjectActivityDto projectActivity;
    private PaymentStatisticsDto paymentStatistics;
    private HourlyChartDto hourlyChart;
    private List<ProjectDetailDto> projectDetails;
    private List<MakerDetailDto> makerDetails;
}
