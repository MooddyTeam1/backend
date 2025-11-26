package com.moa.backend.domain.admin.dto.statistics.funnel;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FunnelStepDto {
    private String stepName;
    private String eventType;
    private Long count;
    private Double conversionRate;
    private Double dropOffRate;
}
