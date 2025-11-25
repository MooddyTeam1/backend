package com.moa.backend.domain.admin.dto.statistics.monthly;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 목표금액 구간 단위
 */
@Getter
@Builder
@AllArgsConstructor
@Schema(description = "목표금액 구간 항목")
public class GoalRangeItemDto {

    @Schema(description = "구간 이름", example = "0~5백만")
    private String rangeName;
    @Schema(description = "성공 프로젝트 수", example = "8")
    private Integer successCount;
    @Schema(description = "전체 프로젝트 수", example = "12")
    private Integer totalCount;
    @Schema(description = "성공률(%)", example = "66.7")
    private Double successRate;
}
