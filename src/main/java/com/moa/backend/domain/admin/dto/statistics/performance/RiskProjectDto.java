package com.moa.backend.domain.admin.dto.statistics.performance;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 위험 프로젝트 정보
 */
@Getter
@Builder
@AllArgsConstructor
@Schema(description = "위험 프로젝트 항목")
public class RiskProjectDto {

    @Schema(description = "프로젝트 ID", example = "1204")
    private Long projectId;
    @Schema(description = "프로젝트명", example = "테이스트키트")
    private String projectName;
    @Schema(description = "메이커명", example = "트레일랩스")
    private String makerName;
    @Schema(description = "위험 사유", example = "달성률 낮음, 남은 일수 5일")
    private String reason;
    @Schema(description = "남은 일수", example = "5")
    private Integer remainingDays;
    @Schema(description = "달성률(%)", example = "40.0")
    private Double achievementRate;
}
