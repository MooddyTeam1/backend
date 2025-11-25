package com.moa.backend.domain.admin.dto.statistics.performance;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * 프로젝트 성과 리포트 최상위 DTO
 */
@Getter
@Builder
@AllArgsConstructor
@Schema(description = "프로젝트 성과 리포트 요약")
public class ProjectPerformanceDto {

    private List<ProjectPerformanceItemDto> projects;
    private List<CategoryAverageDto> categoryAverages;
    private List<MakerAverageDto> makerAverages;
    private List<RiskProjectDto> riskProjects;
    private List<OpportunityProjectDto> opportunityProjects;
}
