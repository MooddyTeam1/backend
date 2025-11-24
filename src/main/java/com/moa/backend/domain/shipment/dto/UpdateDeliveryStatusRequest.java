package com.moa.backend.domain.shipment.dto;

import lombok.*;

/**
 * 한글 설명:
 * - 단건 배송 상태 변경 요청 DTO.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateDeliveryStatusRequest {

    private String status;      // PREPARING/SHIPPING/DELIVERED/CONFIRMED/ISSUE
    private String issueReason; // ISSUE일 때 필수
}
