package com.moa.backend.domain.admin.dto.statistics.monthly;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 카테고리별 성공률 단위
 */
@Getter
@Builder
@AllArgsConstructor
@Schema(description = "카테고리 성공률 항목")
public class CategorySuccessItemDto {

    @Schema(description = "카테고리명", example = "TECH")
    private String categoryName;
    @Schema(description = "전체 프로젝트 수", example = "10")
    private Integer totalCount;
    @Schema(description = "성공 프로젝트 수", example = "7")
    private Integer successCount;
    @Schema(description = "성공률(%)", example = "70.0")
    private Double successRate;
}
