package com.moa.backend.domain.admin.dto.statistics.daily;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 시간대별 데이터 포인트
 */
@Getter
@Builder
@AllArgsConstructor
@Schema(description = "시간대별 데이터 포인트")
public class HourlyDataDto {

    @Schema(description = "시각(0~23)", example = "9")
    private Integer hour;          // 0~23
    @Schema(description = "성공 결제 건수", example = "12")
    private Integer successCount;  // 성공 결제 건수
    @Schema(description = "실패/취소 건수", example = "1")
    private Integer failureCount;  // 실패/취소 건수
    @Schema(description = "성공 결제 금액 합계", example = "250000")
    private Long successAmount;    // 성공 결제 금액 합계
}
