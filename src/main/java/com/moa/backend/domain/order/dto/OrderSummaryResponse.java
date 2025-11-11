package com.moa.backend.domain.order.dto;

import com.moa.backend.domain.order.entity.DeliveryStatus;
import com.moa.backend.domain.order.entity.Order;
import com.moa.backend.domain.order.entity.OrderStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 주문 리스트/상세 공통으로 사용하는 요약 정보 DTO.
 */
@Getter
@Builder
public class OrderSummaryResponse {

    // 주문 PK
    private final Long orderId;
    // 주문 코드(노출용)
    private final String orderCode;
    // 연관 프로젝트 ID
    private final Long projectId;
    // 주문명(배송지 기준)
    private final String orderName;
    // 총 결제 금액
    private final Long totalAmount;
    // 주문 상태 (결제/취소 등)
    private final OrderStatus status;
    // 배송 상태
    private final DeliveryStatus deliveryStatus;
    // 주문 생성 시각
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
