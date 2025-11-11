package com.moa.backend.domain.wallet.entity;

import com.moa.backend.domain.order.entity.Order;
import com.moa.backend.domain.settlement.entity.Settlement;
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
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 프로젝트 지갑의 입출 내역을 저장하는 엔티티.
 * 주문/정산 레퍼런스를 함께 저장해 감사 추적이 가능하도록 한다.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "project_wallet_transactions")
public class ProjectWalletTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 거래가 반영된 프로젝트 지갑
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_wallet_id", nullable = false)
    private ProjectWallet wallet;

    // 거래 종류 (입금/환불 등)
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 30)
    private ProjectWalletTransactionType type;

    // 변동 금액
    @Column(name = "amount", nullable = false)
    private Long amount;

    // 거래 이후 에스크로 잔액
    @Column(name = "balance_after", nullable = false)
    private Long balanceAfter;

    // 주문 기반 거래일 경우 연결
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    // 정산 기반 거래일 경우 연결
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "settlement_id")
    private Settlement settlement;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /**
     * 거래 로그를 생성하는 팩토리 메서드.
     */
    public static ProjectWalletTransaction of(
            ProjectWallet wallet,
            ProjectWalletTransactionType type,
            long amount,
            long balanceAfter,
            Order order,
            Settlement settlement,
            String description
    ) {
        ProjectWalletTransaction transaction = new ProjectWalletTransaction();
        transaction.wallet = wallet;
        transaction.type = type;
        transaction.amount = amount;
        transaction.balanceAfter = balanceAfter;
        transaction.order = order;
        transaction.settlement = settlement;
        transaction.description = description;
        transaction.createdAt = LocalDateTime.now();
        return transaction;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
