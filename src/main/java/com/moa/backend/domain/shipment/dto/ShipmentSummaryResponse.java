package com.moa.backend.domain.shipment.dto;

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
public class ShipmentSummaryResponse {

    private long totalCount;
    private long preparingCount;
    private long shippingCount;
    private long deliveredCount;   // DELIVERED + CONFIRMED 합산
    private long confirmedCount;   // CONFIRMED만 별도
    private long issueCount;
}
