package com.moa.backend.domain.shipment.dto;

import java.util.List;
import lombok.*;

/**
 * 한글 설명: 배송 리스트 + 페이징 응답 DTO.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShipmentListResponse {

    private List<ShipmentListItemResponse> items;
    private int page;
    private int pageSize;
    private long totalElements;
    private int totalPages;
}
