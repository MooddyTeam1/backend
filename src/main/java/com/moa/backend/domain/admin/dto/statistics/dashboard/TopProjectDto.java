package com.moa.backend.domain.admin.dto.statistics.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * 상위 프로젝트 DTO (펀딩액 기준 Top 5)
 */
@Getter
@Builder
@AllArgsConstructor
public class TopProjectDto {

    private Long projectId;          // 프로젝트 ID
    private String projectName;      // 프로젝트명
    private String makerName;        // 메이커명
    private Double achievementRate;  // 달성률 (%)
    private Long fundingAmount;      // 펀딩 금액
    private Integer remainingDays;   // 남은 일수 (D-n)
}
