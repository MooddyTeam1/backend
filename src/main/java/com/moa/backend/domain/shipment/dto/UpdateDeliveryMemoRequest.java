package com.moa.backend.domain.shipment.dto;

import lombok.*;

/**
 * 한글 설명:
 * - 배송 메모 업데이트 요청 DTO.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateDeliveryMemoRequest {

    private String memo;
}
