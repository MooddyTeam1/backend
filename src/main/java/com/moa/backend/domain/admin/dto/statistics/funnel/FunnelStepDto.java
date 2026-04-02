package com.moa.backend.domain.admin.dto.statistics.funnel;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "FunnelStepDto DTO")
public class FunnelStepDto {
    @Schema(description = "stepName", example = "stepName 예시")
    private String stepName;
    @Schema(description = "eventType", example = "eventType 예시")
    private String eventType;
    @Schema(description = "count", example = "1")
    private Long count;
    @Schema(description = "conversionRate", example = "12.5")
    private Double conversionRate;
    @Schema(description = "dropOffRate", example = "12.5")
    private Double dropOffRate;
}

