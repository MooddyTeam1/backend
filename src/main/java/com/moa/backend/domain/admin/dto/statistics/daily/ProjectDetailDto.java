package com.moa.backend.domain.admin.dto.statistics.daily;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 프로젝트별 상세 통계 (방문자 제외)
 */
@Getter
@Builder
@AllArgsConstructor
@Schema(description = "일간 프로젝트 상세 통계")
public class ProjectDetailDto {

    @Schema(description = "프로젝트 ID", example = "1201")
    private Long projectId;
    @Schema(description = "프로젝트명", example = "펄스핏 모듈 밴드")
    private String projectName;
    @Schema(description = "메이커명", example = "메이커원 스튜디오")
    private String makerName;
    @Schema(description = "주문 건수", example = "42")
    private Integer orderCount;
    @Schema(description = "펀딩 금액", example = "450000")
    private Long fundingAmount;
    @Schema(description = "전환율(%)", example = "0.0")
    private Double conversionRate; // 방문자 데이터 미수집 → 0.0으로 반환
}
