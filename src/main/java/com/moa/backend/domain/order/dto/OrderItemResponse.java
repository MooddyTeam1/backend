package com.moa.backend.domain.order.dto;

import com.moa.backend.domain.order.entity.OrderItem;
import lombok.Builder;
import lombok.Getter;

/**
 * 주문 상세 내 각 리워드 항목에 대한 응답.
 */
@Getter
@Builder
public class OrderItemResponse {

    // 리워드 ID (삭제된 경우 null)
    private final Long rewardId;
    // 당시 리워드 이름
    private final String rewardName;
    // 당시 리워드 단가
    private final Long rewardPrice;
    // 주문 수량
    private final Integer quantity;
    // 단가 * 수량
    private final Long subtotal;

    /**
     * OrderItem 엔티티에서 응답 DTO를 생성한다.
     */
    public static OrderItemResponse from(OrderItem item) {
        return OrderItemResponse.builder()
                .rewardId(item.getReward() != null ? item.getReward().getId() : null)
                .rewardName(item.getRewardName())
                .rewardPrice(item.getRewardPrice())
                .quantity(item.getQuantity())
                .subtotal(item.getSubtotal())
                .build();
    }
}
