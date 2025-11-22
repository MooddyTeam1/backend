package com.moa.backend.domain.admin.dto.statistics.monthly;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * 서포터 리텐션 정보
 */
@Getter
@Builder
@AllArgsConstructor
public class RetentionDto {

    private Double repeatSupporterRate;
    private Long existingSupporterCount;
    private Long newSupporterCount;
    private Double existingRatio;
    private Double newRatio;
}
