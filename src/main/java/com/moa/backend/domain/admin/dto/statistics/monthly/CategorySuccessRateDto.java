package com.moa.backend.domain.admin.dto.statistics.monthly;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * 카테고리별 성공률
 */
@Getter
@Builder
@AllArgsConstructor
public class CategorySuccessRateDto {

    private List<CategorySuccessItemDto> categories;
}
