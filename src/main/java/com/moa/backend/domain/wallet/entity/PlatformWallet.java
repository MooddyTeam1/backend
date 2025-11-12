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

/**
 * 플랫폼 전체 자금을 추적하는 싱글턴 지갑.
 * PG 입금, 메이커 송금, 환불, 수수료 내역을 누적 관리한다.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "platform_wallets")
public class PlatformWallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 플랫폼 지갑 총 잔액
    @Column(name = "total_balance", nullable = false)
    private Long totalBalance = 0L;

    // 프로젝트로부터 받은 누적 입금
    @Column(name = "total_project_deposit", nullable = false)
    private Long totalProjectDeposit = 0L;

    // 메이커에게 송금한 누적 금액
    @Column(name = "total_maker_payout", nullable = false)
    private Long totalMakerPayout = 0L;

    // 플랫폼 수수료 수익 누계
    @Column(name = "total_platform_fee", nullable = false)
    private Long totalPlatformFee = 0L;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * 최초 생성 시 기본값을 설정한다.
     */
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

    /**
     * PG에서 순수 결제 금액이 입금됐을 때 호출한다.
     */
    public void deposit(long amount) {
        this.totalBalance += amount;
        this.totalProjectDeposit += amount;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 메이커 송금 시 호출하며 잔액 검증을 수행한다.
     */
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

    /**
     * 고객 환불로 송금된 금액을 차감한다.
     */
    public void refundOut(long amount) {
        if (this.totalBalance < amount) {
            throw new AppException(ErrorCode.INSUFFICIENT_PLATFORM_BALANCE);
        }
        this.totalBalance -= amount;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 플랫폼 수수료 수익 누계 기록.
     */
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
