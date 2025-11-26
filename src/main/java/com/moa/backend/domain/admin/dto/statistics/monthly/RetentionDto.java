package com.moa.backend.domain.admin.dto.statistics.monthly;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 서포터 리텐션 정보
 */
@Getter
@Builder
@AllArgsConstructor
@Schema(description = "서포터 리텐션 요약")
public class RetentionDto {

    @Schema(description = "재방문 서포터 비율(%)", example = "68.0")
    private Double repeatSupporterRate;
    @Schema(description = "기존 서포터 수", example = "320")
    private Long existingSupporterCount;
    @Schema(description = "신규 서포터 수", example = "150")
    private Long newSupporterCount;
    @Schema(description = "기존 서포터 비중(%)", example = "68.1")
    private Double existingRatio;
    @Schema(description = "신규 서포터 비중(%)", example = "31.9")
    private Double newRatio;
}
