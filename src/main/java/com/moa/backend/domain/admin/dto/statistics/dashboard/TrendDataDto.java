package com.moa.backend.domain.admin.dto.statistics.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * 일별 트렌드 데이터 DTO
 */
@Getter
@Builder
@AllArgsConstructor
public class TrendDataDto {

    private String date;            // 날짜 (MM/dd 형식)
    private Long fundingAmount;     // 펀딩 금액
    private Integer projectCount;   // 프로젝트 수
    private Integer orderCount;     // 주문 건수
}
