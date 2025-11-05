package com.moa.backend.domain.order.dto.response;

import com.moa.backend.domain.order.entity.OrderItem;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrderItemResponse {

    private final Long rewardId;
    private final String rewardName;
    private final Long rewardPrice;
    private final Integer quantity;
    private final Long subtotal;

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
