package com.moa.backend.domain.admin.dto.statistics.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * 개별 카테고리 성과 DTO
 */
@Getter
@Builder
@AllArgsConstructor
public class CategoryItemDto {

    private String categoryName;    // 카테고리명
    private Long fundingAmount;     // 펀딩 금액
    private Integer projectCount;   // 프로젝트 수
    private Integer orderCount;     // 주문 건수
    private Double fundingRatio;    // 펀딩 비율 (%)
}
