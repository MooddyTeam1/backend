package com.moa.backend.domain.order.dto;

import com.moa.backend.domain.order.entity.Order;
import com.moa.backend.domain.payment.entity.Payment;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "주문 상세 응답")
public class OrderDetailResponse {

    // 주문 기본 정보
    @Schema(description = "주문 요약 정보")
    private final OrderSummaryResponse summary;
    // 배송, 수령인 정보
    @Schema(description = "배송/수령인 정보")
    private final ShippingInfo shipping;
    // 결제, 영수증 정보(없을 수 있음)
    @Schema(description = "결제 정보")
    private final PaymentInfo payment;
    @Schema(description = "영수증 URL", example = "https://pay.moa.com/receipt/abc123")
    private final String receiptUrl;
    // 배송 타임라인
    @Schema(description = "배송/확정 타임라인")
    private final Timeline timeline;
    // 주문에 포함된 리워드 항목들
    @Schema(description = "주문 리워드 항목 목록")
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
        @Schema(description = "수령인 이름", example = "홍길동")
        private final String receiverName;
        @Schema(description = "수령인 연락처", example = "010-1234-5678")
        private final String receiverPhone;
        @Schema(description = "주소1", example = "서울특별시 강남구 테헤란로 1")
        private final String addressLine1;
        @Schema(description = "주소2", example = "101동 202호")
        private final String addressLine2;
        @Schema(description = "우편번호", example = "06234")
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
        @Schema(description = "결제 수단", example = "CARD")
        private final String method;
        @Schema(description = "마스킹된 카드번호", example = "1234-56**-****-7890")
        private final String cardMasked;
        @Schema(description = "결제 승인 시각", example = "2025-01-05T12:00:00")
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
        @Schema(description = "배송 시작 시각", example = "2025-01-06T09:00:00")
        private final LocalDateTime deliveryStartedAt;
        @Schema(description = "배송 완료 시각", example = "2025-01-08T18:00:00")
        private final LocalDateTime deliveryCompletedAt;
        @Schema(description = "주문 확정 시각", example = "2025-01-09T12:00:00")
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
