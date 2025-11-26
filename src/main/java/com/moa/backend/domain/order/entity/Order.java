package com.moa.backend.domain.order.entity;

import com.moa.backend.domain.project.entity.Project;
import com.moa.backend.domain.user.entity.User;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 한글 설명:
 * - 서포터가 프로젝트 리워드를 구매할 때 생성되는 주문 엔티티.
 * - 결제 상태(OrderStatus), 배송 상태(DeliveryStatus), 배송지 정보를 모두 보관한다.
 * - 배송 콘솔에서 사용하는 배송 단위도 Order를 기준으로 삼는다.
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

    // 한글 설명: 외부에 노출되는 주문 코드(주문번호). 중복 불가.
    @Column(name = "order_id", nullable = false, unique = true, length = 64)
    private String orderCode;

    // 한글 설명: 주문한 서포터(User).
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 한글 설명: 주문이 속한 프로젝트.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    // 한글 설명: 주문 총 결제 금액 (모든 리워드 합계).
    @Column(name = "total_amount", nullable = false)
    private Long totalAmount;

    // 한글 설명: 결제 상태 (PENDING/PAID/CANCELED).
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private OrderStatus status;

    // 한글 설명: 주문/배송명 (목록에서 보이는 주문 타이틀).
    @Column(name = "order_name", nullable = false, length = 200)
    private String orderName;

    // ====== 배송지 / 수령인 정보 ======

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

    // ====== 배송 상태 및 타임라인 ======

    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_status", length = 20)
    private DeliveryStatus deliveryStatus = DeliveryStatus.NONE;

    // 한글 설명: 배송(택배) 출발 시각. (발송일)
    @Column(name = "delivery_started_at")
    private LocalDateTime deliveryStartedAt;

    // 한글 설명: 배송이 플랫폼 기준으로 완료 처리된 시각.
    @Column(name = "delivery_completed_at")
    private LocalDateTime deliveryCompletedAt;

    // 한글 설명: 서포터가 '수령 완료'를 눌러 구매 확정을 한 시각.
    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    // ====== 송장 / 배송 메타 정보 ======

    // 한글 설명: 택배사 이름 (CJ대한통운, 한진, 롯데 등).
    @Column(name = "courier_name", length = 100)
    private String courierName;

    // 한글 설명: 운송장 번호 (문자/숫자 혼합 가능).
    @Column(name = "tracking_number", length = 100)
    private String trackingNumber;

    // 한글 설명: 메이커 내부용 배송 메모.
    @Column(name = "delivery_memo", length = 1000)
    private String deliveryMemo;

    // 한글 설명: 배송 문제/보류 사유 (ISSUE 상태일 때 사용).
    @Column(name = "delivery_issue_reason", length = 1000)
    private String deliveryIssueReason;

    // ====== 주문 항목 (리워드 목록) ======

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    // ====== 공통 생성/수정 시각 ======

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

    // ====== 정적 팩토리 & 연관관계 메서드 ======

    /**
     * 한글 설명: 주문을 초기 상태(PENDING)로 생성한다.
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
     * 한글 설명: 주문 품목을 추가하고 양방향 연관관계를 고정한다.
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

    /** 한글 설명: 결제 완료 처리. */
    public void markPaid() {
        this.status = OrderStatus.PAID;
    }

    /** 한글 설명: 주문 취소 처리. */
    public void cancel() {
        this.status = OrderStatus.CANCELED;
    }

    // ====== 배송 도메인 메서드 ======

    /**
     * 한글 설명:
     * - 배송을 시작(발송)할 때 호출.
     * - 상태를 SHIPPING 으로 전환하고, deliveryStartedAt을 세팅한다.
     */
    public void startDelivery() {
        if (this.status != OrderStatus.PAID) {
            throw new IllegalStateException("결제가 완료된 주문만 배송을 시작할 수 있습니다.");
        }
        this.deliveryStatus = DeliveryStatus.SHIPPING;
        if (this.deliveryStartedAt == null) {
            this.deliveryStartedAt = LocalDateTime.now();
        }
    }

    /**
     * 한글 설명:
     * - 플랫폼/메이커 기준으로 '배송 완료' 처리할 때 사용.
     * - 상태를 DELIVERED로 전환하고, deliveryCompletedAt을 세팅한다.
     */
    public void completeDelivery() {
        if (this.deliveryStatus != DeliveryStatus.SHIPPING
                && this.deliveryStatus != DeliveryStatus.PREPARING) {
            throw new IllegalStateException("배송중 또는 준비중 상태에서만 배송 완료로 변경할 수 있습니다.");
        }
        this.deliveryStatus = DeliveryStatus.DELIVERED;
        this.deliveryCompletedAt = LocalDateTime.now();
    }

    /**
     * 한글 설명:
     * - 서포터가 '수령 완료' 버튼을 눌렀을 때 호출.
     * - 배송 상태를 CONFIRMED로 전환하고 confirmedAt을 기록한다.
     */
    public void confirmBySupporter() {
        if (this.deliveryStatus != DeliveryStatus.SHIPPING
                && this.deliveryStatus != DeliveryStatus.DELIVERED
                && this.deliveryStatus != DeliveryStatus.CONFIRMED) {
            throw new IllegalStateException("배송중/배송완료 상태에서만 수령 확인이 가능합니다.");
        }
        this.deliveryStatus = DeliveryStatus.CONFIRMED;
        this.confirmedAt = LocalDateTime.now();
        if (this.deliveryCompletedAt == null) {
            this.deliveryCompletedAt = this.confirmedAt;
        }
    }
    /**
     * 한글 설명:
     * - 기존 코드(스케줄러 등) 호환용 구매 확정 메서드.
     * - 내부적으로는 confirmBySupporter() 로직을 재사용한다.
     * - 자동 확정/사용자 확정 모두 "CONFIRMED" 상태로 만드는 공통 진입점이다.
     */
    public void confirm() {
        this.confirmBySupporter();
    }

    /**
     * 한글 설명:
     * - 택배사/운송장 정보를 입력 또는 수정할 때 사용.
     * - autoStartDelivery=true 인 경우, 배송 상태가 NONE/PREPARING이면 SHIPPING 으로 전환한다.
     */
    public void updateTrackingInfo(String courierName, String trackingNumber, boolean autoStartDelivery) {
        this.courierName = courierName;
        this.trackingNumber = trackingNumber;

        if (autoStartDelivery
                && (this.deliveryStatus == DeliveryStatus.NONE || this.deliveryStatus == DeliveryStatus.PREPARING)) {
            this.startDelivery();
        }
    }

    /**
     * 한글 설명:
     * - 배송 문제/보류 상태로 전환할 때 사용.
     * - 상태를 ISSUE로 바꾸고, 사유를 기록한다.
     */
    public void markIssue(String reason) {
        this.deliveryStatus = DeliveryStatus.ISSUE;
        this.deliveryIssueReason = reason;
    }
}
