package com.moa.backend.domain.settlement.entity;

import com.moa.backend.domain.project.entity.Project;
import com.moa.backend.domain.user.entity.User;
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
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "settlement")
public class Settlement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false, unique = true)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "creator_user_id", nullable = false)
    private User creator;

    @Column(name = "total_order_amount", nullable = false)
    private Long totalOrderAmount;

    @Column(name = "toss_fee_amount", nullable = false)
    private Long tossFeeAmount;

    @Column(name = "platform_fee_amount", nullable = false)
    private Long platformFeeAmount;

    @Column(name = "net_amount", nullable = false)
    private Long netAmount;

    @Column(name = "first_payment_amount", nullable = false)
    private Long firstPaymentAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "first_payment_status", nullable = false, length = 20)
    private SettlementPayoutStatus firstPaymentStatus;

    @Column(name = "first_payment_at")
    private LocalDateTime firstPaymentAt;

    @Column(name = "final_payment_amount", nullable = false)
    private Long finalPaymentAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "final_payment_status", nullable = false, length = 20)
    private SettlementPayoutStatus finalPaymentStatus;

    @Column(name = "final_payment_at")
    private LocalDateTime finalPaymentAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private SettlementStatus status;

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

