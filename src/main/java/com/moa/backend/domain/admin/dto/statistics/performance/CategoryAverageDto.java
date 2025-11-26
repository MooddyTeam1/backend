package com.moa.backend.domain.admin.dto.statistics.performance;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 카테고리별 평균 지표
 */
@Getter
@Builder
@AllArgsConstructor
@Schema(description = "카테고리 평균 지표")
public class CategoryAverageDto {

    @Schema(description = "카테고리명", example = "TECH")
    private String categoryName;
    @Schema(description = "평균 달성률(%)", example = "110.0")
    private Double averageAchievementRate;
    @Schema(description = "평균 모금액", example = "350000")
    private Long averageFundingAmount;
    @Schema(description = "성공률(%)", example = "70.0")
    private Double successRate;
}
