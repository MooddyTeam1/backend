package com.moa.backend.domain.maker.dto.manageproject;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 한글 설명: 프로젝트 일별 통계 DTO.
 * - 일별 방문수 / 신규 서포터 수 / 모금액을 담는다.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DailyStatsResponse DTO")
public class DailyStatsResponse {

    // 한글 설명: 날짜 (YYYY-MM-DD)
    @Schema(description = "date", example = "2025-11-01")
    private LocalDate date;

    // 한글 설명: 해당 일의 방문수 (세션 기준, 중복 제거)
    @Schema(description = "views", example = "1")
    private Integer views;

    // 한글 설명: 해당 일의 신규 서포터 수
    @Schema(description = "supporters", example = "1")
    private Integer supporters;

    // 한글 설명: 해당 일의 모금액 합계 (원)
    @Schema(description = "amount", example = "1")
    private Long amount;
}
