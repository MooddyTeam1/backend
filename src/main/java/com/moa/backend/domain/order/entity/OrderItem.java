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

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reward_id")
    private Reward reward;

    @Column(name = "reward_name", nullable = false, length = 200)
    private String rewardName;

    @Column(name = "reward_price", nullable = false)
    private Long rewardPrice;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "subtotal", nullable = false)
    private Long subtotal;

    @Column(name = "note", length = 255)
    private String note;

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

    void assignOrder(Order order) {
        this.order = order;
    }
}

