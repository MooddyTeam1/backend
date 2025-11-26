package com.moa.backend.domain.order.dto;

import com.moa.backend.domain.order.entity.OrderItem;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

/**
 * 주문 상세 내 각 리워드 항목에 대한 응답.
 */
@Getter
@Builder
@Schema(description = "주문 리워드 항목 응답")
public class OrderItemResponse {

    // 리워드 ID (삭제된 경우 null)
    @Schema(description = "리워드 ID(삭제 시 null)", example = "1001")
    private final Long rewardId;
    // 당시 리워드 이름
    @Schema(description = "리워드 이름", example = "텀블러 단품")
    private final String rewardName;
    // 당시 리워드 단가
    @Schema(description = "리워드 단가", example = "19000")
    private final Long rewardPrice;
    // 주문 수량
    @Schema(description = "수량", example = "2")
    private final Integer quantity;
    // 단가 * 수량
    @Schema(description = "소계(단가*수량)", example = "38000")
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
