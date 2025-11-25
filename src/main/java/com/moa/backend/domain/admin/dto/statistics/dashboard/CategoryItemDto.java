package com.moa.backend.domain.admin.dto.statistics.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 개별 카테고리 성과 DTO
 */
@Getter
@Builder
@AllArgsConstructor
@Schema(description = "카테고리 성과 항목")
public class CategoryItemDto {

    @Schema(description = "카테고리명", example = "TECH")
    private String categoryName;    // 카테고리명
    @Schema(description = "펀딩 금액", example = "1500000")
    private Long fundingAmount;     // 펀딩 금액
    @Schema(description = "프로젝트 수", example = "12")
    private Integer projectCount;   // 프로젝트 수
    @Schema(description = "주문 건수", example = "320")
    private Integer orderCount;     // 주문 건수
    @Schema(description = "펀딩 비율(%)", example = "28.5")
    private Double fundingRatio;    // 펀딩 비율 (%)
}
