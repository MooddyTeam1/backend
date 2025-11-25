package com.moa.backend.domain.admin.dto.statistics.performance;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 메이커별 평균 지표
 */
@Getter
@Builder
@AllArgsConstructor
@Schema(description = "메이커 평균 지표")
public class MakerAverageDto {

    @Schema(description = "메이커 ID", example = "1003")
    private Long makerId;
    @Schema(description = "메이커명", example = "메이커원 스튜디오")
    private String makerName;
    @Schema(description = "프로젝트 수", example = "3")
    private Integer projectCount;
    @Schema(description = "평균 모금액", example = "400000")
    private Long averageFundingAmount;
    @Schema(description = "성공률(%)", example = "80.0")
    private Double successRate;
    @Schema(description = "첫 프로젝트 여부", example = "false")
    private Boolean isFirstProject;
}
