package com.moa.backend.domain.shipment.dto;

import lombok.*;

/**
 * 한글 설명:
 * - 송장 정보 업데이트 요청 DTO.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateTrackingInfoRequest {

    private String courierName;
    private String trackingNumber;
    private boolean autoStartDelivery;
}
