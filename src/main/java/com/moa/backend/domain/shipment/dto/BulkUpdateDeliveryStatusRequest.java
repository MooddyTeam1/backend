package com.moa.backend.domain.shipment.dto;

import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * 한글 설명:
 * - 일괄 배송 상태 변경 요청 DTO.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "배송 상태 일괄 변경 요청")
public class BulkUpdateDeliveryStatusRequest {

    @Schema(description = "주문 ID 목록", example = "[5001,5002,5003]")
    private List<Long> orderIds;
    @Schema(description = "배송 상태", example = "SHIPPING")
    private String status;
    @Schema(description = "이슈 사유(ISSUE 시)", example = "주소 불명")
    private String issueReason;
}
