package com.moa.backend.domain.admin.dto.statistics.monthly;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 서포터 요약 (월별)
 */
@Getter
@Builder
@AllArgsConstructor
@Schema(description = "월간 서포터 요약")
public class MonthlySupporterSummaryDto {

    @Schema(description = "재방문 서포터 수", example = "320")
    private Long repeatSupporterCount;
    @Schema(description = "신규 서포터 수", example = "150")
    private Long newSupporterCount;
}
