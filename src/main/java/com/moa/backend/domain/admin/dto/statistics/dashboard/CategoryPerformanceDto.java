package com.moa.backend.domain.admin.dto.statistics.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * 카테고리별 성과 DTO (Top 4)
 */
@Getter
@Builder
@AllArgsConstructor
public class CategoryPerformanceDto {

    private List<CategoryItemDto> categories;  // 상위 4개 카테고리
}
