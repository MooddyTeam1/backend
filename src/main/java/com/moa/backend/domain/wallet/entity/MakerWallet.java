package com.moa.backend.domain.wallet.entity;

import com.moa.backend.domain.maker.entity.Maker;
import com.moa.backend.global.error.AppException;
import com.moa.backend.global.error.ErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 메이커 개인의 정산 지갑.
 * 선지급/잔금/출금 흐름을 기록하며 가용 잔액·대기 잔액을 관리한다.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "maker_wallets")
public class MakerWallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "maker_id", nullable = false, unique = true)
    private Maker maker;

    // 출금 가능한 가용 잔액
    @Column(name = "available_balance", nullable = false)
    private Long availableBalance = 0L;

    // 잔금 지급 대기 중인 금액
    @Column(name = "pending_balance", nullable = false)
    private Long pendingBalance = 0L;

    // 누적 적립 금액(선지급+잔금)
    @Column(name = "total_earned", nullable = false)
    private Long totalEarned = 0L;

    // 메이커가 실제 출금한 누적 금액
    @Column(name = "total_withdrawn", nullable = false)
    private Long totalWithdrawn = 0L;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public static MakerWallet of(Maker maker) {
        MakerWallet wallet = new MakerWallet();
        wallet.maker = maker;
        wallet.availableBalance = 0L;
        wallet.pendingBalance = 0L;
        wallet.totalEarned = 0L;
        wallet.totalWithdrawn = 0L;
        wallet.updatedAt = LocalDateTime.now();
        return wallet;
    }

    /**
     * 잔금 지급 대기 금액(pending) 적립.
     */
    public void addPending(long amount) {
        this.pendingBalance += amount;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 선지급 등 즉시 사용 가능한 금액 적립.
     */
    public void creditAvailable(long amount) {
        this.availableBalance += amount;
        this.totalEarned += amount;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 잔금 지급 시 pending → available 이동.
     */
    public void releasePendingToAvailable(long amount) {
        if (this.pendingBalance < amount) {
            throw new AppException(
                    ErrorCode.INSUFFICIENT_AVAILABLE_BALANCE,
                    "대기 잔액 부족: 현재=" + this.pendingBalance + ", 요청=" + amount
            );
        }
        this.pendingBalance -= amount;
        this.availableBalance += amount;
        this.totalEarned += amount;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 환불 등으로 이미 지급된 금액 회수.
     */
    public void refundDebit(long amount) {
        if (this.availableBalance < amount) {
            throw new AppException(ErrorCode.INSUFFICIENT_AVAILABLE_BALANCE);
        }
        this.availableBalance -= amount;
        this.totalEarned -= amount;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 메이커 출금 처리.
     */
    public void withdraw(long amount) {
        if (this.availableBalance < amount) {
            throw new AppException(ErrorCode.INSUFFICIENT_AVAILABLE_BALANCE);
        }
        this.availableBalance -= amount;
        this.totalWithdrawn += amount;
        this.updatedAt = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
