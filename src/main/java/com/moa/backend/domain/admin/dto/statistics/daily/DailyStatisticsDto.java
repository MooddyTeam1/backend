package com.moa.backend.domain.admin.dto.statistics.daily;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * 일일 통계 최상위 DTO
 */
@Getter
@Builder
@AllArgsConstructor
@Schema(description = "일간 통계 요약")
public class DailyStatisticsDto {

    @Schema(description = "트래픽 요약")
    private TrafficDto traffic;
    @Schema(description = "프로젝트 활동 요약")
    private ProjectActivityDto projectActivity;
    @Schema(description = "결제 통계")
    private PaymentStatisticsDto paymentStatistics;
    @Schema(description = "시간대별 차트")
    private HourlyChartDto hourlyChart;
    @Schema(description = "프로젝트 상세 리스트")
    private List<ProjectDetailDto> projectDetails;
    @Schema(description = "메이커 상세 리스트")
    private List<MakerDetailDto> makerDetails;
}
