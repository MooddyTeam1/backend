package com.moa.backend.domain.admin.dto.statistics.daily;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 메이커별 상세 통계
 */
@Getter
@Builder
@AllArgsConstructor
@Schema(description = "일간 메이커 상세 통계")
public class MakerDetailDto {

    @Schema(description = "메이커 ID", example = "1003")
    private Long makerId;
    @Schema(description = "메이커명", example = "메이커원 스튜디오")
    private String makerName;
    @Schema(description = "프로젝트 수", example = "3")
    private Integer projectCount;
    @Schema(description = "주문 건수", example = "40")
    private Integer orderCount;
    @Schema(description = "펀딩 금액", example = "800000")
    private Long fundingAmount;
}
