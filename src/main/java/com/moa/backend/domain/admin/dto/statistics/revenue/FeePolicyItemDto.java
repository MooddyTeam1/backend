package com.moa.backend.domain.admin.dto.statistics.revenue;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * 수수료 정책별 요약 (현재 일반 프로젝트만 사용)
 */
@Getter
@Builder
@AllArgsConstructor
public class FeePolicyItemDto {

    private String policyName;
    private Integer projectCount;
    private Long paymentAmount;
    private Long feeAmount;
    private Double contributionRate;
}
