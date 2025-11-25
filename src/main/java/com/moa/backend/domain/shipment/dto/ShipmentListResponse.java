package com.moa.backend.domain.shipment.dto;

import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * 한글 설명: 배송 리스트 + 페이징 응답 DTO.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "배송 목록 페이지 응답")
public class ShipmentListResponse {

    @Schema(description = "배송 항목 목록")
    private List<ShipmentListItemResponse> items;
    @Schema(description = "현재 페이지(1-base 아님 여부 확인)", example = "1")
    private int page;
    @Schema(description = "페이지 크기", example = "50")
    private int pageSize;
    @Schema(description = "전체 건수", example = "120")
    private long totalElements;
    @Schema(description = "전체 페이지 수", example = "3")
    private int totalPages;
}
