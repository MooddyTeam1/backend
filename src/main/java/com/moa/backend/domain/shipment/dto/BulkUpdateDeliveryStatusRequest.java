package com.moa.backend.domain.shipment.dto;

import java.util.List;
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
public class BulkUpdateDeliveryStatusRequest {

    private List<Long> orderIds;
    private String status;
    private String issueReason;
}
