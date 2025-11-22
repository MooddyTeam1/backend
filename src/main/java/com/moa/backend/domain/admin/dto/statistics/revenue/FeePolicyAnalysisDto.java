package com.moa.backend.domain.admin.dto.statistics.revenue;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * 수수료 정책 분석 (현재는 일반 프로젝트만)
 */
@Getter
@Builder
@AllArgsConstructor
public class FeePolicyAnalysisDto {

    private List<FeePolicyItemDto> policies;
}
