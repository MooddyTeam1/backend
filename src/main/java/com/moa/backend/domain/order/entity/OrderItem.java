package com.moa.backend.domain.order.entity;

import com.moa.backend.domain.reward.entity.Reward;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 주문에 포함된 리워드 단위 항목.
 * 주문 생성 시점의 리워드 정보 스냅샷을 저장한다.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 소속 주문
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    // 실제 리워드 엔티티 (필수 아님)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reward_id")
    private Reward reward;

    // 주문 당시 리워드명
    @Column(name = "reward_name", nullable = false, length = 200)
    private String rewardName;

    // 주문 당시 리워드 단가
    @Column(name = "reward_price", nullable = false)
    private Long rewardPrice;

    // 구매 수량
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    // 단가 * 수량
    @Column(name = "subtotal", nullable = false)
    private Long subtotal;

    // 옵션 메모
    @Column(name = "note", length = 255)
    private String note;

    /**
     * 리워드 정보를 기반으로 OrderItem을 생성한다.
     */
    public static OrderItem of(Reward reward, String rewardName, Long rewardPrice, Integer quantity, String note) {
        OrderItem item = new OrderItem();
        item.reward = reward;
        item.rewardName = rewardName;
        item.rewardPrice = rewardPrice;
        item.quantity = quantity;
        item.subtotal = rewardPrice * quantity;
        item.note = note;
        return item;
    }

    /** 부모 주문과의 연관관계 설정 */
    void assignOrder(Order order) {
        this.order = order;
    }
}

