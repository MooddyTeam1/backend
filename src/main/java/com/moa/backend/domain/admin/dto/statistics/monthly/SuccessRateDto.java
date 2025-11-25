package com.moa.backend.domain.admin.dto.statistics.monthly;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 성공률 정보 (시작 기준/종료 기준)
 */
@Getter
@Builder
@AllArgsConstructor
@Schema(description = "성공률 정보")
public class SuccessRateDto {

    @Schema(description = "시작 기준 성공률")
    private SuccessRateItemDto startBased;
    @Schema(description = "종료 기준 성공률")
    private SuccessRateItemDto endBased;
}
