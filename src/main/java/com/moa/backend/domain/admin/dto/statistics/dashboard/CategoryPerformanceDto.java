package com.moa.backend.domain.admin.dto.statistics.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * 카테고리별 성과 DTO (Top 4)
 */
@Getter
@Builder
@AllArgsConstructor
@Schema(description = "카테고리 성과 Top 목록")
public class CategoryPerformanceDto {

    @Schema(description = "상위 카테고리 목록")
    private List<CategoryItemDto> categories;  // 상위 4개 카테고리
}
