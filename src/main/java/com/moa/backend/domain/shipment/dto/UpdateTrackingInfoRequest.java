package com.moa.backend.domain.shipment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "송장 정보 업데이트 요청")
public class UpdateTrackingInfoRequest {

    @Schema(description = "택배사 이름", example = "CJ대한통운")
    private String courierName;
    @Schema(description = "송장번호", example = "1234-5678-9012")
    private String trackingNumber;
    @Schema(description = "송장 입력 시 자동 배송시작 처리 여부", example = "true")
    private boolean autoStartDelivery;
}
