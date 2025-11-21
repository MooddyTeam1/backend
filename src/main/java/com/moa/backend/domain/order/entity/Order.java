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
import lombok.Setter;

/**
 * 서포터가 프로젝트 리워드를 구매할 때 생성되는 주문 엔티티.
 * 결제/배송/확정 상태와 배송지 정보를 모두 보관한다.
 */
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 노출용 주문 코드 (중복 불가)
    @Column(name = "order_id", nullable = false, unique = true, length = 64)
    private String orderCode;

    // 주문한 서포터
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 연결된 프로젝트
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    // 주문 총 금액
    @Column(name = "total_amount", nullable = false)
    private Long totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private OrderStatus status;

    // 주문/배송명 (수령인 표시용)
    @Column(name = "order_name", nullable = false, length = 200)
    private String orderName;

    // 수령인 정보
    @Column(name = "receiver_name", nullable = false, length = 100)
    private String receiverName;

    @Column(name = "receiver_phone", nullable = false, length = 50)
    private String receiverPhone;

    // 배송지 주소
    @Column(name = "address_line1", nullable = false, length = 255)
    private String addressLine1;

    @Column(name = "address_line2", length = 255)
    private String addressLine2;

    // 우편번호
    @Column(name = "zip_code", nullable = false, length = 20)
    private String zipCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_status", length = 20)
    private DeliveryStatus deliveryStatus = DeliveryStatus.NONE;

    // 배송 진행 타임라인
    @Column(name = "delivery_started_at")
    private LocalDateTime deliveryStartedAt;

    @Column(name = "delivery_completed_at")
    private LocalDateTime deliveryCompletedAt;

    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    // 주문에 포함된 리워드 항목들
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

    /**
     * 주문을 초기 상태(PENDING)로 생성한다.
     */
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

    /**
     * 주문 품목을 추가하고 양방향 연관관계를 고정한다.
     */
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

    /** 결제 완료 처리 */
    public void markPaid() {
        this.status = OrderStatus.PAID;
    }

    /** 주문 취소 처리 */
    public void cancel() {
        this.status = OrderStatus.CANCELED;
    }

    /**
     * 결제 완료 이후 배송을 시작한다.
     */
    public void startDelivery() {
        if (this.status != OrderStatus.PAID) {
            throw new IllegalStateException("Only paid orders can start delivery");
        }
        this.deliveryStatus = DeliveryStatus.SHIPPING;
        this.deliveryStartedAt = LocalDateTime.now();
    }

    /**
     * 배송 완료 처리.
     */
    public void completeDelivery() {
        if (this.deliveryStatus != DeliveryStatus.SHIPPING) {
            throw new IllegalStateException("Delivery can be completed only from SHIPPING status");
        }
        this.deliveryStatus = DeliveryStatus.DELIVERED;
        this.deliveryCompletedAt = LocalDateTime.now();
    }

    /**
     * 구매자 확정을 기록한다.
     */
    public void confirm() {
        if (this.deliveryStatus != DeliveryStatus.DELIVERED) {
            throw new IllegalStateException("Confirmation is allowed only after delivery is completed");
        }
        this.deliveryStatus = DeliveryStatus.CONFIRMED;
        this.confirmedAt = LocalDateTime.now();
    }
}

