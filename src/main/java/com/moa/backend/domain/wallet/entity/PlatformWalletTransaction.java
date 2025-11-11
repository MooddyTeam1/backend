package com.moa.backend.domain.wallet.entity;

import com.moa.backend.domain.project.entity.Project;
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
 * 플랫폼 지갑 거래 로그 엔티티.
 * 프로젝트/정산 기준으로 어떤 금액 변동이 있었는지 저장한다.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "platform_wallet_transactions")
public class PlatformWalletTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 거래가 반영된 플랫폼 지갑
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "wallet_id", nullable = false)
    private PlatformWallet wallet;

    // PG 입금/메이커 송금 등 거래 종류
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 30)
    private PlatformWalletTransactionType type;

    // 변동 금액 (출금 시 음수)
    @Column(name = "amount", nullable = false)
    private Long amount;

    // 거래 이후 플랫폼 총 잔액
    @Column(name = "balance_after", nullable = false)
    private Long balanceAfter;

    // 특정 프로젝트 관련 거래인 경우
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "related_project_id")
    private Project relatedProject;

    // 특정 정산과 연결된 경우
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "related_settlement_id")
    private Settlement relatedSettlement;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /**
     * 거래 로그 생성을 위한 팩토리.
     */
    public static PlatformWalletTransaction of(
            PlatformWallet wallet,
            PlatformWalletTransactionType type,
            long amount,
            long balanceAfter,
            Project project,
            Settlement settlement,
            String description
    ) {
        PlatformWalletTransaction transaction = new PlatformWalletTransaction();
        transaction.wallet = wallet;
        transaction.type = type;
        transaction.amount = amount;
        transaction.balanceAfter = balanceAfter;
        transaction.relatedProject = project;
        transaction.relatedSettlement = settlement;
        transaction.description = description;
        transaction.createdAt = LocalDateTime.now();
        return transaction;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
