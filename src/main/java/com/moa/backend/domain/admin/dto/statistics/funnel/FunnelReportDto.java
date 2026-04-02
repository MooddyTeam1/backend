package com.moa.backend.domain.admin.dto.statistics.funnel;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "FunnelReportDto DTO")
public class FunnelReportDto {
    @Schema(description = "startDate", example = "2025-11-01")
    private LocalDate startDate;
    @Schema(description = "endDate", example = "2025-11-01")
    private LocalDate endDate;
    @Schema(description = "steps", example = "[]")
    private List<FunnelStepDto> steps;
    @Schema(description = "totalConversionRate", example = "12.5")
    private Double totalConversionRate;
}

