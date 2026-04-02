package com.moa.backend.domain.maker.dto.manageproject;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 한글 설명: 채널별 유입 통계 응답 DTO.
 * - 도넛 차트, 바 차트에서 사용.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "ChannelStatsResponse DTO")
public class ChannelStatsResponse {

    // 한글 설명: 채널 이름 (직접 방문, 검색, 인스타그램, 블로그, 카카오톡, 기타 등)
    @Schema(description = "channel", example = "channel")
    private String channel;

    // 한글 설명: 해당 채널에서 발생한 유입 횟수
    @Schema(description = "count", example = "1")
    private Integer count;

    // 한글 설명: 전체 유입 중 이 채널이 차지하는 비율 (% 단위, 소수점 1자리)
    @Schema(description = "percentage", example = "12.5")
    private Double percentage;
}
