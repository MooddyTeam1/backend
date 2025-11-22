package com.moa.backend.domain.admin.dto.statistics.monthly;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * 서포터 요약 (월별)
 */
@Getter
@Builder
@AllArgsConstructor
public class MonthlySupporterSummaryDto {

    private Long repeatSupporterCount;
    private Long newSupporterCount;
}
