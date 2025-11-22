package com.moa.backend.domain.admin.dto.statistics.daily;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * 트래픽 관련 지표 (UV/PV/재방문율은 추후 연동 예정)
 */
@Getter
@Builder
@AllArgsConstructor
public class TrafficDto {

    private Long uniqueVisitors;  // 추후 구현 (현재 0 반환)
    private Long pageViews;       // 추후 구현 (현재 0 반환)
    private Long newUsers;        // 신규 가입자 수
    private Double returningRate; // 추후 구현 (현재 0.0 반환)
}
