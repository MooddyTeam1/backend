package com.moa.backend.domain.admin.dto.statistics.monthly;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * 카테고리별 성공률
 */
@Getter
@Builder
@AllArgsConstructor
@Schema(description = "카테고리별 성공률 목록")
public class CategorySuccessRateDto {

    @Schema(description = "카테고리 성공률 리스트")
    private List<CategorySuccessItemDto> categories;
}
