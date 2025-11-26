package com.moa.backend.domain.admin.dto.statistics.revenue;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 수익 리포트 상세 행 (일자/프로젝트 단위)
 */
@Getter
@Builder
@AllArgsConstructor
@Schema(description = "수익 리포트 상세(일자/프로젝트 단위)")
public class RevenueDetailDto {

    @Schema(description = "날짜", example = "2025-11-05")
    private String date;             // yyyy-MM-dd
    @Schema(description = "프로젝트 ID", example = "1201")
    private Long projectId;
    @Schema(description = "프로젝트명", example = "펄스핏 모듈 밴드")
    private String projectName;
    @Schema(description = "메이커명", example = "메이커원 스튜디오")
    private String makerName;
    @Schema(description = "결제 금액", example = "150000")
    private Long paymentAmount;
    @Schema(description = "PG 수수료", example = "22500")
    private Long pgFee;
    @Schema(description = "플랫폼 수수료", example = "15000")
    private Long platformFee;
    @Schema(description = "메이커 정산 금액", example = "112500")
    private Long makerSettlementAmount;
    @Schema(description = "정산 상태", example = "FIRST_PAID")
    private String settlementStatus;
}
