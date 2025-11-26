package com.moa.backend.domain.admin.dto.statistics.revenue;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 수수료 정책별 요약 (현재 일반 프로젝트만 사용)
 */
@Getter
@Builder
@AllArgsConstructor
@Schema(description = "수수료 정책 항목")
public class FeePolicyItemDto {

    @Schema(description = "정책 이름", example = "일반 수수료")
    private String policyName;
    @Schema(description = "적용 프로젝트 수", example = "25")
    private Integer projectCount;
    @Schema(description = "총 결제 금액", example = "1500000")
    private Long paymentAmount;
    @Schema(description = "수수료 금액", example = "99000")
    private Long feeAmount;
    @Schema(description = "기여율(%)", example = "6.6")
    private Double contributionRate;
}
