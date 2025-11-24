package com.moa.backend.domain.shipment.dto;

import com.moa.backend.domain.order.entity.Order;
import com.moa.backend.domain.order.entity.OrderItem;
import com.moa.backend.domain.order.entity.DeliveryStatus;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

/**
 * 한글 설명:
 * - 메이커 배송 관리 화면의 리스트 테이블 한 행에 해당하는 DTO.
 */
@Getter
@Builder
public class ShipmentListItemResponse {

    private Long orderId;
    private String orderCode;
    private String supporterName;
    private String supporterPhone;
    private String address;          // [우편번호] + 주소1 + 주소2

    private String rewardSummary;    // 대표 리워드명 + " 외 N건"
    private Integer totalQuantity;   // 전체 수량 합계
    private Long amount;             // 주문 총 금액

    private DeliveryStatus deliveryStatus;
    private String courierName;
    private String trackingNumber;

    private LocalDateTime createdAt;
    private LocalDateTime shippedAt;
    private LocalDateTime deliveredAt;

    // 한글 설명: Order 엔티티를 기반으로 리스트용 DTO 생성
    public static ShipmentListItemResponse fromOrder(Order order) {
        // 1) 서포터 이름/연락처
        String supporterName = order.getUser().getName(); // or order.getReceiverName()
        String supporterPhone = order.getReceiverPhone();

        // 2) 주소 문자열 조합
        StringBuilder addressBuilder = new StringBuilder();
        if (order.getZipCode() != null) {
            addressBuilder.append("[").append(order.getZipCode()).append("] ");
        }
        addressBuilder.append(order.getAddressLine1() != null ? order.getAddressLine1() : "");
        if (order.getAddressLine2() != null && !order.getAddressLine2().isBlank()) {
            addressBuilder.append(" ").append(order.getAddressLine2());
        }
        String fullAddress = addressBuilder.toString();

        // 3) 리워드 요약 정보
        List<OrderItem> items = order.getOrderItems();
        String rewardSummary;
        int totalQty = 0;
        if (items == null || items.isEmpty()) {
            rewardSummary = "(리워드 없음)";
        } else {
            OrderItem first = items.get(0);
            rewardSummary = first.getRewardName();
            if (items.size() > 1) {
                rewardSummary += " 외 " + (items.size() - 1) + "건";
            }
            for (OrderItem item : items) {
                totalQty += item.getQuantity();
            }
        }

        return ShipmentListItemResponse.builder()
                .orderId(order.getId())
                .orderCode(order.getOrderCode())
                .supporterName(supporterName)
                .supporterPhone(supporterPhone)
                .address(fullAddress)
                .rewardSummary(rewardSummary)
                .totalQuantity(totalQty)
                .amount(order.getTotalAmount())
                .deliveryStatus(order.getDeliveryStatus())
                .courierName(order.getCourierName())
                .trackingNumber(order.getTrackingNumber())
                .createdAt(order.getCreatedAt())
                .shippedAt(order.getDeliveryStartedAt())
                .deliveredAt(order.getDeliveryCompletedAt())
                .build();
    }
}
