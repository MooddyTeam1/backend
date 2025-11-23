package com.moa.backend.domain.admin.dto.statistics.daily;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * 프로젝트별 상세 통계 (방문자 제외)
 */
@Getter
@Builder
@AllArgsConstructor
public class ProjectDetailDto {

    private Long projectId;
    private String projectName;
    private String makerName;
    private Integer orderCount;
    private Long fundingAmount;
    private Double conversionRate; // 방문자 데이터 미수집 → 0.0으로 반환
}
