package com.moa.backend.domain.wallet.entity;

import com.moa.backend.global.error.AppException;
import com.moa.backend.global.error.ErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "platform_wallets")
public class PlatformWallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "total_balance", nullable = false)
    private Long totalBalance = 0L;

    @Column(name = "total_project_deposit", nullable = false)
    private Long totalProjectDeposit = 0L;

    @Column(name = "total_maker_payout", nullable = false)
    private Long totalMakerPayout = 0L;

    @Column(name = "total_platform_fee", nullable = false)
    private Long totalPlatformFee = 0L;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public static PlatformWallet initialize() {
        PlatformWallet wallet = new PlatformWallet();
        wallet.totalBalance = 0L;
        wallet.totalProjectDeposit = 0L;
        wallet.totalMakerPayout = 0L;
        wallet.totalPlatformFee = 0L;
        wallet.createdAt = LocalDateTime.now();
        wallet.updatedAt = wallet.createdAt;
        return wallet;
    }

    public void deposit(long amount) {
        this.totalBalance += amount;
        this.totalProjectDeposit += amount;
        this.updatedAt = LocalDateTime.now();
    }

    public void withdraw(long amount) {
        if (this.totalBalance < amount) {
            throw new AppException(
                    ErrorCode.INSUFFICIENT_PLATFORM_BALANCE,
                    "플랫폼 잔액 부족: 현재=" + this.totalBalance + ", 요청=" + amount
            );
        }
        this.totalBalance -= amount;
        this.totalMakerPayout += amount;
        this.updatedAt = LocalDateTime.now();
    }

    public void refundOut(long amount) {
        if (this.totalBalance < amount) {
            throw new AppException(ErrorCode.INSUFFICIENT_PLATFORM_BALANCE);
        }
        this.totalBalance -= amount;
        this.updatedAt = LocalDateTime.now();
    }

    public void recordPlatformFee(long amount) {
        this.totalPlatformFee += amount;
        this.updatedAt = LocalDateTime.now();
    }

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
