package com.moa.backend.domain.admin.dto.statistics.daily;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * 메이커별 상세 통계
 */
@Getter
@Builder
@AllArgsConstructor
public class MakerDetailDto {

    private Long makerId;
    private String makerName;
    private Integer projectCount;
    private Integer orderCount;
    private Long fundingAmount;
}
