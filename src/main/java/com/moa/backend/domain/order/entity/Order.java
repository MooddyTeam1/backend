package com.moa.backend.domain.order.entity;

import com.moa.backend.domain.project.entity.Project;
import com.moa.backend.domain.user.entity.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", nullable = false, unique = true, length = 64)
    private String orderCode;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
   private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(name = "total_amount", nullable = false)
    private Long totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private OrderStatus status;

    @Column(name = "order_name", nullable = false, length = 200)
    private String orderName;

    @Column(name = "receiver_name", nullable = false, length = 100)
    private String receiverName;

    @Column(name = "receiver_phone", nullable = false, length = 50)
    private String receiverPhone;

    @Column(name = "address_line1", nullable = false, length = 255)
    private String addressLine1;

    @Column(name = "address_line2", length = 255)
    private String addressLine2;

    @Column(name = "zip_code", nullable = false, length = 20)
    private String zipCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_status", length = 20)
    private DeliveryStatus deliveryStatus;

    @Column(name = "delivery_started_at")
    private LocalDateTime deliveryStartedAt;

    @Column(name = "delivery_completed_at")
    private LocalDateTime deliveryCompletedAt;

    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public static Order create(
            User user,
            Project project,
            String orderCode,
            String orderName,
            Long totalAmount,
            String receiverName,
            String receiverPhone,
            String addressLine1,
            String addressLine2,
            String zipCode
    ) {
        Order order = new Order();
        order.user = user;
        order.project = project;
        order.orderCode = orderCode;
        order.orderName = orderName;
        order.totalAmount = totalAmount;
        order.status = OrderStatus.PENDING;
        order.receiverName = receiverName;
        order.receiverPhone = receiverPhone;
        order.addressLine1 = addressLine1;
        order.addressLine2 = addressLine2;
        order.zipCode = zipCode;
        return order;
    }

    public void addItem(OrderItem orderItem) {
        this.orderItems.add(orderItem);
        orderItem.assignOrder(this);
    }

    public void updateTotalAmount(Long totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void updateOrderName(String orderName) {
        this.orderName = orderName;
    }

    public void markPaid() {
        this.status = OrderStatus.PAID;
    }

    public void cancel() {
        this.status = OrderStatus.CANCELED;
    }

    public void startDelivery() {
        if (this.status != OrderStatus.PAID) {
            throw new IllegalStateException("Only paid orders can start delivery");
        }
        this.deliveryStatus = DeliveryStatus.SHIPPING;
        this.deliveryStartedAt = LocalDateTime.now();
    }

    public void completeDelivery() {
        if (this.deliveryStatus != DeliveryStatus.SHIPPING) {
            throw new IllegalStateException("Delivery can be completed only from SHIPPING status");
        }
        this.deliveryStatus = DeliveryStatus.DELIVERED;
        this.deliveryCompletedAt = LocalDateTime.now();
    }

    public void confirm() {
        if (this.deliveryStatus != DeliveryStatus.DELIVERED) {
            throw new IllegalStateException("Confirmation is allowed only after delivery is completed");
        }
        this.deliveryStatus = DeliveryStatus.CONFIRMED;
        this.confirmedAt = LocalDateTime.now();
    }
}

