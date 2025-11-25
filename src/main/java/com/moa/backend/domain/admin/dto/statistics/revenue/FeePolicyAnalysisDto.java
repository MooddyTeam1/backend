package com.moa.backend.domain.admin.dto.statistics.revenue;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * 수수료 정책 분석 (현재는 일반 프로젝트만)
 */
@Getter
@Builder
@AllArgsConstructor
@Schema(description = "수수료 정책 분석 요약")
public class FeePolicyAnalysisDto {

    @Schema(description = "수수료 정책 목록")
    private List<FeePolicyItemDto> policies;
}
