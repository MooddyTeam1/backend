package com.moa.backend.domain.admin.dto.statistics.daily;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * 시간대별 데이터 포인트
 */
@Getter
@Builder
@AllArgsConstructor
public class HourlyDataDto {

    private Integer hour;          // 0~23
    private Integer successCount;  // 성공 결제 건수
    private Integer failureCount;  // 실패/취소 건수
    private Long successAmount;    // 성공 결제 금액 합계
}
