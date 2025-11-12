package com.moa.backend.domain.wallet.entity;

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
 * 메이커 지갑의 입출 로그.
 * Settlement/설명과 함께 잔액 스냅샷을 저장해 감사 추적을 돕는다.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "wallet_transactions")
public class WalletTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 거래가 발생한 메이커 지갑
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "wallet_id", nullable = false)
    private MakerWallet wallet;

    // 입금/출금/정산 등 거래 종류
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private WalletTransactionType type;

    // 변동 금액 (음수 포함)
    @Column(name = "amount", nullable = false)
    private Long amount;

    // 거래 처리 후 잔액 스냅샷
    @Column(name = "balance_after", nullable = false)
    private Long balanceAfter;

    // 어떤 정산과 연결된 거래인지 (선택)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "settlement_id")
    private Settlement settlement;

    // 추가 설명(주문번호 등)
    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /**
     * 생성 시각 자동 기록.
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}

