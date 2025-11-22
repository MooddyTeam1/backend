package com.moa.backend.domain.admin.dto.statistics.performance;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * 프로젝트별 상세 성과
 */
@Getter
@Builder
@AllArgsConstructor
public class ProjectPerformanceItemDto {

    private Long projectId;
    private String projectName;
    private String makerName;
    private String category;
    private Long fundingAmount;
    private Double achievementRate;
    private Integer supporterCount;
    private Long averageSupportAmount;
    private Long bookmarkCount;
    private Double conversionRate; // 방문자 데이터 미수집 → 0.0 반환
    private Integer remainingDays;
}
