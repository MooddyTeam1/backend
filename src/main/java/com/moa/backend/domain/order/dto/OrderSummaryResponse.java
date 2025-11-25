package com.moa.backend.domain.order.dto;

import com.moa.backend.domain.order.entity.DeliveryStatus;
import com.moa.backend.domain.order.entity.Order;
import com.moa.backend.domain.order.entity.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 주문 리스트/상세 공통으로 사용하는 요약 정보 DTO.
 */
@Getter
@Builder
@Schema(description = "주문 요약 정보")
public class OrderSummaryResponse {

    // 주문 PK
    @Schema(description = "주문 ID", example = "5001")
    private final Long orderId;
    // 주문 코드(노출용)
    @Schema(description = "주문 코드", example = "ORD-20250105-0001")
    private final String orderCode;
    // 연관 프로젝트 ID
    @Schema(description = "프로젝트 ID", example = "101")
    private final Long projectId;
    // 주문명(배송지 기준)
    @Schema(description = "주문명", example = "홍길동님 주문")
    private final String orderName;
    // 총 결제 금액
    @Schema(description = "총 결제 금액(원)", example = "29000")
    private final Long totalAmount;
    // 주문 상태 (결제/취소 등)
    @Schema(description = "주문 상태", example = "PAID")
    private final OrderStatus status;
    // 배송 상태
    @Schema(description = "배송 상태", example = "DELIVERING")
    private final DeliveryStatus deliveryStatus;
    // 주문 생성 시각
    @Schema(description = "주문 생성 시각", example = "2025-01-05T12:00:00")
    private final LocalDateTime createdAt;

    /**
     * Order 엔티티를 요약 응답으로 변환한다.
     */
    public static OrderSummaryResponse from(Order order) {
        return OrderSummaryResponse.builder()
                .orderId(order.getId())
                .orderCode(order.getOrderCode())
                .projectId(order.getProject() != null ? order.getProject().getId() : null)
                .orderName(order.getOrderName())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .deliveryStatus(order.getDeliveryStatus())
                .createdAt(order.getCreatedAt())
                .build();
    }
}
