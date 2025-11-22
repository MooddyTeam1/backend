package com.moa.backend.domain.admin.dto.statistics.monthly;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * 카테고리별 성공률 단위
 */
@Getter
@Builder
@AllArgsConstructor
public class CategorySuccessItemDto {

    private String categoryName;
    private Integer totalCount;
    private Integer successCount;
    private Double successRate;
}
