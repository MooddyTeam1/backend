package com.moa.backend.domain.admin.dto.statistics.monthly;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 성공률 단위 정보
 */
@Getter
@Builder
@AllArgsConstructor
@Schema(description = "성공률 항목")
public class SuccessRateItemDto {

    @Schema(description = "성공 프로젝트 수", example = "10")
    private Integer successCount;
    @Schema(description = "전체 프로젝트 수", example = "16")
    private Integer totalCount;
    @Schema(description = "성공률(%)", example = "62.5")
    private Double rate;
}
