package com.moa.backend.domain.admin.dto.statistics.performance;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * 카테고리별 평균 지표
 */
@Getter
@Builder
@AllArgsConstructor
public class CategoryAverageDto {

    private String categoryName;
    private Double averageAchievementRate;
    private Long averageFundingAmount;
    private Double successRate;
}
