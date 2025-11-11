package com.moa.backend.domain.settlement.entity;

import com.moa.backend.domain.maker.entity.Maker;
import com.moa.backend.domain.project.entity.Project;
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
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 프로젝트 단위 정산 정보를 보관하는 엔티티.
 * 총 주문금액, 수수료, 선지급/잔금 지급 상태를 한 번에 추적한다.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "settlements")
public class Settlement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 대상 프로젝트 (1:1)
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false, unique = true)
    private Project project;

    // 정산 받을 메이커
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "maker_id", nullable = false)
    private Maker maker;

    // 총 주문 금액
    @Column(name = "total_order_amount", nullable = false)
    private Long totalOrderAmount;

    // PG(토스) 수수료
    @Column(name = "toss_fee_amount", nullable = false)
    private Long tossFeeAmount;

    // 플랫폼 수수료
    @Column(name = "platform_fee_amount", nullable = false)
    private Long platformFeeAmount;

    // 메이커에게 지급 가능한 금액 (총액 - 수수료)
    @Column(name = "net_amount", nullable = false)
    private Long netAmount;

    // 선지급 금액
    @Column(name = "first_payment_amount", nullable = false)
    private Long firstPaymentAmount;

    // 선지급 상태
    @Enumerated(EnumType.STRING)
    @Column(name = "first_payment_status", nullable = false, length = 20)
    private SettlementPayoutStatus firstPaymentStatus;

    // 선지급 완료 시각
    @Column(name = "first_payment_at")
    private LocalDateTime firstPaymentAt;

    // 잔금 금액
    @Column(name = "final_payment_amount", nullable = false)
    private Long finalPaymentAmount;

    // 잔금 상태
    @Enumerated(EnumType.STRING)
    @Column(name = "final_payment_status", nullable = false, length = 20)
    private SettlementPayoutStatus finalPaymentStatus;

    // 잔금 완료 시각
    @Column(name = "final_payment_at")
    private LocalDateTime finalPaymentAt;

    // 전체 정산 상태 (선지급/잔금 프로세스)
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private SettlementStatus status;

    // 재시도 횟수(예: PG 통신 실패)
    @Column(name = "retry_count", nullable = false)
    private Integer retryCount = 0;

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
}

