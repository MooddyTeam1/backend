package com.moa.backend.domain.admin.dto.statistics.revenue;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 플랫폼 수익 요약
 */
@Getter
@Builder
@AllArgsConstructor
@Schema(description = "플랫폼 수익 요약")
public class PlatformRevenueDto {

    @Schema(description = "총 결제액", example = "15000000")
    private Long totalPaymentAmount;
    @Schema(description = "PG 수수료 금액", example = "300000")
    private Long pgFeeAmount;
    @Schema(description = "PG 수수료율(%)", example = "2.0")
    private Double pgFeeRate;
    @Schema(description = "플랫폼 수수료 금액", example = "700000")
    private Long platformFeeAmount;
    @Schema(description = "플랫폼 수수료율(%)", example = "10.0")
    private Double platformFeeRate;
    @Schema(description = "기타 비용", example = "50000")
    private Long otherCosts;
    // 메이커 지급액(총 결제액 - PG 수수료 - 플랫폼 수수료)
    @Schema(description = "메이커 지급액", example = "14000000")
    private Long netPayoutToMaker;
    // 플랫폼 순이익(플랫폼 수수료 - PG 수수료 - 기타 비용)
    @Schema(description = "플랫폼 순이익", example = "350000")
    private Long netPlatformProfit;
}
