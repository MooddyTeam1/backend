package com.moa.backend.domain.admin.dto.statistics.daily;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 프로젝트 활동 통계
 */
@Getter
@Builder
@AllArgsConstructor
@Schema(description = "프로젝트 활동 통계")
public class ProjectActivityDto {

    @Schema(description = "신규 프로젝트 수", example = "5")
    private Long newProjectCount;        // 신규 프로젝트 생성
    @Schema(description = "심사 요청 수(REVIEW)", example = "3")
    private Long reviewRequestedCount;   // 심사 요청(REVIEW)
    @Schema(description = "승인 완료 수(APPROVED)", example = "4")
    private Long approvedCount;          // 승인 완료(APPROVED)
    @Schema(description = "당일 종료 프로젝트 수", example = "2")
    private Long closedTodayCount;       // 종료된 프로젝트(END_DATE 기준)
}
