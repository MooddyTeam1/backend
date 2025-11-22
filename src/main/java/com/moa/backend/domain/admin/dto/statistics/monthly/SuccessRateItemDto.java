package com.moa.backend.domain.admin.dto.statistics.monthly;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * 성공률 단위 정보
 */
@Getter
@Builder
@AllArgsConstructor
public class SuccessRateItemDto {

    private Integer successCount;
    private Integer totalCount;
    private Double rate;
}
