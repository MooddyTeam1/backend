package com.moa.backend.domain.admin.dto.statistics.monthly;

import com.moa.backend.domain.admin.dto.statistics.dashboard.KpiItemDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 월별 KPI 묶음
 */
@Getter
@Builder
@AllArgsConstructor
@Schema(description = "월간 KPI 묶음")
public class MonthlyKpiDto {

    @Schema(description = "총 펀딩 금액 KPI")
    private KpiItemDto totalFundingAmount;
    @Schema(description = "성공 프로젝트 수", example = "12")
    private Integer successProjectCount;
    @Schema(description = "실패 프로젝트 수", example = "3")
    private Integer failedProjectCount;
    @Schema(description = "신규 메이커 수", example = "2")
    private Integer newMakerCount;
    @Schema(description = "신규 서포터 수", example = "120")
    private Integer newSupporterCount;
}
