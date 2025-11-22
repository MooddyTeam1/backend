package com.moa.backend.domain.admin.dto.statistics.performance;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * 위험 프로젝트 정보
 */
@Getter
@Builder
@AllArgsConstructor
public class RiskProjectDto {

    private Long projectId;
    private String projectName;
    private String makerName;
    private String reason;
    private Integer remainingDays;
    private Double achievementRate;
}
