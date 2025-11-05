package com.moa.backend.domain.order.dto.response;

import com.moa.backend.domain.order.entity.Order;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrderDetailResponse {

    private final OrderSummaryResponse summary;
    private final LocalDateTime deliveryStartedAt;
    private final LocalDateTime deliveryCompletedAt;
    private final LocalDateTime confirmedAt;
    private final List<OrderItemResponse> items;

    public static OrderDetailResponse from(Order order) {
        List<OrderItemResponse> itemResponses = order.getOrderItems().stream()
                .map(OrderItemResponse::from)
                .collect(Collectors.toList());

        return OrderDetailResponse.builder()
                .summary(OrderSummaryResponse.from(order))
                .deliveryStartedAt(order.getDeliveryStartedAt())
                .deliveryCompletedAt(order.getDeliveryCompletedAt())
                .confirmedAt(order.getConfirmedAt())
                .items(itemResponses)
                .build();
    }
}
