package com.moa.backend.domain.shipment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "단건 배송 상태 변경 요청")
public class UpdateDeliveryStatusRequest {

    @Schema(description = "배송 상태", example = "SHIPPING")
    private String status;      // PREPARING/SHIPPING/DELIVERED/CONFIRMED/ISSUE
    @Schema(description = "이슈 사유(ISSUE일 때 필수)", example = "주소 불명")
    private String issueReason; // ISSUE일 때 필수
}
