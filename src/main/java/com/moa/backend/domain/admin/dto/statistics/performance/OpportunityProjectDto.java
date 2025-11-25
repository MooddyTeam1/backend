package com.moa.backend.domain.admin.dto.statistics.performance;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 기회 프로젝트 정보
 */
@Getter
@Builder
@AllArgsConstructor
@Schema(description = "기회 프로젝트 항목")
public class OpportunityProjectDto {

    @Schema(description = "프로젝트 ID", example = "1205")
    private Long projectId;
    @Schema(description = "프로젝트명", example = "홈라이트")
    private String projectName;
    @Schema(description = "메이커명", example = "메이커원 스튜디오")
    private String makerName;
    @Schema(description = "기회 사유", example = "달성률 높음, 남은 일수 30일")
    private String reason;
    @Schema(description = "남은 일수", example = "30")
    private Integer remainingDays;
    @Schema(description = "달성률(%)", example = "180.0")
    private Double achievementRate;
}
