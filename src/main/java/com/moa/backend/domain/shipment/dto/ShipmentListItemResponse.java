package com.moa.backend.domain.shipment.dto;

import com.moa.backend.domain.order.entity.Order;
import com.moa.backend.domain.order.entity.OrderItem;
import com.moa.backend.domain.order.entity.DeliveryStatus;
import java.time.LocalDateTime;
import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

/**
 * 한글 설명:
 * - 메이커 배송 관리 화면의 리스트 테이블 한 행에 해당하는 DTO.
 */
@Getter
@Builder
@Schema(description = "배송 리스트 항목 응답")
public class ShipmentListItemResponse {

    @Schema(description = "주문 ID", example = "1400")
    private Long orderId;
    @Schema(description = "주문 코드", example = "ORD-20251101-AAA")
    private String orderCode;
    @Schema(description = "수령인 이름", example = "서포터1")
    private String supporterName;
    @Schema(description = "수령인 연락처", example = "010-2000-0001")
    private String supporterPhone;
    @Schema(description = "주소", example = "[06236] 서울시 강남구 강남대로 321 501호")
    private String address;          // [우편번호] + 주소1 + 주소2

    @Schema(description = "리워드 요약", example = "펄스핏 스타터 패키지 외 1건")
    private String rewardSummary;    // 대표 리워드명 + " 외 N건"
    @Schema(description = "총 수량", example = "2")
    private Integer totalQuantity;   // 전체 수량 합계
    @Schema(description = "주문 총 금액", example = "150000")
    private Long amount;             // 주문 총 금액

    @Schema(description = "배송 상태", example = "NONE")
    private DeliveryStatus deliveryStatus;
    @Schema(description = "택배사", example = "CJ대한통운")
    private String courierName;
    @Schema(description = "송장번호", example = "1234-5678-9012")
    private String trackingNumber;

    @Schema(description = "주문 생성 시각", example = "2025-11-01T10:15:00")
    private LocalDateTime createdAt;
    @Schema(description = "배송 시작 시각", example = "2025-11-02T09:00:00")
    private LocalDateTime shippedAt;
    @Schema(description = "배송 완료 시각", example = "2025-11-04T18:00:00")
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
