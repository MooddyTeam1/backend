package com.moa.backend.domain.admin.dto.statistics.monthly;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * 월별 일자별 트렌드 데이터
 */
@Getter
@Builder
@AllArgsConstructor
public class MonthlyTrendDataDto {

    private String date;          // MM/dd
    private Long fundingAmount;
    private Integer projectCount;
    private Integer orderCount;
}
