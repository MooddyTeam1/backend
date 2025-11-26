package com.moa.backend.domain.admin.dto.statistics.funnel;

import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FunnelReportDto {
    private LocalDate startDate;
    private LocalDate endDate;
    private List<FunnelStepDto> steps;
    private Double totalConversionRate;
}
