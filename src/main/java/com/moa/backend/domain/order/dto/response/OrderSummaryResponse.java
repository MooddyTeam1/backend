package com.moa.backend.domain.order.dto.response;

import com.moa.backend.domain.order.entity.Order;
import com.moa.backend.domain.order.entity.DeliveryStatus;
import com.moa.backend.domain.order.entity.OrderStatus;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrderSummaryResponse {

    private final Long orderId;
    private final String orderCode;
    private final Long projectId;
    private final String orderName;
    private final Long totalAmount;
    private final OrderStatus status;
    private final DeliveryStatus deliveryStatus;
    private final LocalDateTime createdAt;

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
