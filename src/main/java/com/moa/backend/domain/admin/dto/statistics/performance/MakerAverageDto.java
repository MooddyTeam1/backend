package com.moa.backend.domain.admin.dto.statistics.performance;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * 메이커별 평균 지표
 */
@Getter
@Builder
@AllArgsConstructor
public class MakerAverageDto {

    private Long makerId;
    private String makerName;
    private Integer projectCount;
    private Long averageFundingAmount;
    private Double successRate;
    private Boolean isFirstProject;
}
