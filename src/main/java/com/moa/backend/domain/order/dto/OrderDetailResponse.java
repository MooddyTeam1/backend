package com.moa.backend.domain.order.dto;

import com.moa.backend.domain.order.entity.Order;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 주문 상세 응답 DTO.
 * 주문 요약 정보와 배송/확정 시각, 아이템 목록을 함께 제공한다.
 */
@Getter
@Builder
public class OrderDetailResponse {

    // 주문 기본 정보
    private final OrderSummaryResponse summary;
    // 배송 시작 시각
    private final LocalDateTime deliveryStartedAt;
    // 배송 완료 시각
    private final LocalDateTime deliveryCompletedAt;
    // 구매 확정 시각
    private final LocalDateTime confirmedAt;
    // 주문에 포함된 리워드 항목들
    private final List<OrderItemResponse> items;

    /**
     * Order 엔티티를 상세 응답으로 변환한다.
     */
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
