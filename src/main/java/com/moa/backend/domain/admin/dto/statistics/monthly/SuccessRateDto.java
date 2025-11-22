package com.moa.backend.domain.admin.dto.statistics.monthly;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * 성공률 정보 (시작 기준/종료 기준)
 */
@Getter
@Builder
@AllArgsConstructor
public class SuccessRateDto {

    private SuccessRateItemDto startBased;
    private SuccessRateItemDto endBased;
}
