package com.moa.backend.domain.admin.dto.statistics.performance;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 프로젝트별 상세 성과
 */
@Getter
@Builder
@AllArgsConstructor
@Schema(description = "프로젝트별 성과 항목")
public class ProjectPerformanceItemDto {

    @Schema(description = "프로젝트 ID", example = "1201")
    private Long projectId;
    @Schema(description = "프로젝트명", example = "펄스핏 모듈 밴드")
    private String projectName;
    @Schema(description = "메이커명", example = "메이커원 스튜디오")
    private String makerName;
    @Schema(description = "카테고리", example = "TECH")
    private String category;
    @Schema(description = "펀딩 금액", example = "450000")
    private Long fundingAmount;
    @Schema(description = "달성률(%)", example = "132.0")
    private Double achievementRate;
    @Schema(description = "서포터 수", example = "300")
    private Integer supporterCount;
    @Schema(description = "1인당 평균 후원액", example = "150000")
    private Long averageSupportAmount;
    @Schema(description = "북마크 수", example = "123")
    private Long bookmarkCount;
    @Schema(description = "전환률(%)", example = "0.0")
    private Double conversionRate; // 방문자 데이터 미수집 → 0.0 반환
    @Schema(description = "남은 일수", example = "20")
    private Integer remainingDays;
}
