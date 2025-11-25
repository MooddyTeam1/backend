package com.moa.backend.domain.shipment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * 한글 설명:
 * - 상단 요약 카드 영역 데이터 DTO.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "배송 요약 응답")
public class ShipmentSummaryResponse {

    @Schema(description = "총 주문 수", example = "120")
    private long totalCount;
    @Schema(description = "배송 준비중 수", example = "30")
    private long preparingCount;
    @Schema(description = "배송 중 수", example = "50")
    private long shippingCount;
    @Schema(description = "배송 완료 수(DELIVERED+CONFIRMED)", example = "35")
    private long deliveredCount;   // DELIVERED + CONFIRMED 합산
    @Schema(description = "수령 확정 수(CONFIRMED)", example = "20")
    private long confirmedCount;   // CONFIRMED만 별도
    @Schema(description = "배송 이슈 수", example = "5")
    private long issueCount;
}
