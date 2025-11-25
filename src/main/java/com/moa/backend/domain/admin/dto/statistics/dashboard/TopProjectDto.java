package com.moa.backend.domain.admin.dto.statistics.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 상위 프로젝트 DTO (펀딩액 기준 Top 5)
 */
@Getter
@Builder
@AllArgsConstructor
@Schema(description = "대시보드 Top 프로젝트")
public class TopProjectDto {

    @Schema(description = "프로젝트 ID", example = "1201")
    private Long projectId;          // 프로젝트 ID
    @Schema(description = "프로젝트명", example = "펄스핏 모듈 밴드")
    private String projectName;      // 프로젝트명
    @Schema(description = "메이커명", example = "메이커원 스튜디오")
    private String makerName;        // 메이커명
    @Schema(description = "달성률(%)", example = "132.0")
    private Double achievementRate;  // 달성률 (%)
    @Schema(description = "펀딩 금액", example = "450000")
    private Long fundingAmount;      // 펀딩 금액
    @Schema(description = "남은 일수(D-n)", example = "20")
    private Integer remainingDays;   // 남은 일수 (D-n)
}
