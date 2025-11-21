package com.moa.backend.domain.order.dto;

import com.moa.backend.domain.order.entity.Order;
import com.moa.backend.domain.payment.entity.Payment;
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
    // 배송, 수령인 정보
    private final ShippingInfo shipping;
    // 결제, 영수증 정보(없을 수 있음)
    private final PaymentInfo payment;
    private final String receiptUrl;
    // 배송 타임라인
    private final Timeline timeline;
    // 주문에 포함된 리워드 항목들
    private final List<OrderItemResponse> items;

    /**
     * Order 엔티티를 상세 응답으로 변환한다.
     */
    public static OrderDetailResponse from(Order order) {
        return from(order, null);
    }

    /**
     * Order + Payment 엔티티를 상세 응답으로 변환한다.
     */
    public static OrderDetailResponse from(Order order, Payment payment) {
        List<OrderItemResponse> itemResponses = order.getOrderItems().stream()
                .map(OrderItemResponse::from)
                .collect(Collectors.toList());

        return OrderDetailResponse.builder()
                .summary(OrderSummaryResponse.from(order))
                .shipping(ShippingInfo.from(order))
                .payment(PaymentInfo.from(payment))
                .receiptUrl(payment != null ? payment.getReceiptUrl() : null)
                .timeline(Timeline.from(order))
                .items(itemResponses)
                .build();
    }

    @Getter
    @Builder
    public static class ShippingInfo {
        private final String receiverName;
        private final String receiverPhone;
        private final String addressLine1;
        private final String addressLine2;
        private final String zipCode;

        /**
         * 주문 엔티티에서 배송/수령 정보만 추출한다.
         */
        public static ShippingInfo from(Order order) {
            return ShippingInfo.builder()
                    .receiverName(order.getReceiverName())
                    .receiverPhone(order.getReceiverPhone())
                    .addressLine1(order.getAddressLine1())
                    .addressLine2(order.getAddressLine2())
                    .zipCode(order.getZipCode())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class PaymentInfo {
        private final String method;
        private final String cardMasked;
        private final LocalDateTime paidAt;

        /**
         * 결제 정보가 없으면 null을 반환한다.
         */
        public static PaymentInfo from(Payment payment) {
            if (payment == null) {
                return null;
            }
            return PaymentInfo.builder()
                    .method(payment.getMethod())
                    .cardMasked(payment.getCardMasked())
                    .paidAt(payment.getApprovedAt())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class Timeline {
        private final LocalDateTime deliveryStartedAt;
        private final LocalDateTime deliveryCompletedAt;
        private final LocalDateTime confirmedAt;

        /**
         * 주문 엔티티의 배송 타임라인만 묶어 반환한다.
         */
        public static Timeline from(Order order) {
            return Timeline.builder()
                    .deliveryStartedAt(order.getDeliveryStartedAt())
                    .deliveryCompletedAt(order.getDeliveryCompletedAt())
                    .confirmedAt(order.getConfirmedAt())
                    .build();
        }
    }
}
