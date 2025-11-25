package com.moa.backend.domain.admin.dto.statistics.daily;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 트래픽 관련 지표 (UV/PV/재방문율은 추후 연동 예정)
 */
@Getter
@Builder
@AllArgsConstructor
@Schema(description = "트래픽 지표")
public class TrafficDto {

    @Schema(description = "고유 방문자 수", example = "12345")
    private Long uniqueVisitors;  // 추후 구현 (현재 0 반환)
    @Schema(description = "페이지뷰", example = "56789")
    private Long pageViews;       // 추후 구현 (현재 0 반환)
    @Schema(description = "신규 가입자 수", example = "120")
    private Long newUsers;        // 신규 가입자 수
    @Schema(description = "재방문율(%)", example = "35.2")
    private Double returningRate; // 추후 구현 (현재 0.0 반환)
}
