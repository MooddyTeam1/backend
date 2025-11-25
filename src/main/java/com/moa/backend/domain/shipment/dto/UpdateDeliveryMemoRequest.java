package com.moa.backend.domain.shipment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "배송 메모 업데이트 요청")
public class UpdateDeliveryMemoRequest {

    @Schema(description = "배송 메모", example = "경비실에 맡겨주세요")
    private String memo;
}
