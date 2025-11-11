package com.moa.backend.domain.wallet.entity;

import com.moa.backend.domain.project.entity.Project;
import com.moa.backend.global.error.AppException;
import com.moa.backend.global.error.ErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
 * 프로젝트 단위 에스크로 지갑.
 * 결제 승인 금액을 보관하고 정산 시 hold/release 흐름을 추적한다.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "project_wallets")
public class ProjectWallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 이 지갑이 속한 프로젝트
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false, unique = true)
    private Project project;

    // 에스크로로 묶여 있는 금액
    @Column(name = "escrow_balance", nullable = false)
    private Long escrowBalance = 0L;

    // 정산 대기(hold) 금액
    @Column(name = "pending_release", nullable = false)
    private Long pendingRelease = 0L;

    // 지금까지 release된 누적 금액
    @Column(name = "released_total", nullable = false)
    private Long releasedTotal = 0L;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ProjectWalletStatus status = ProjectWalletStatus.ACTIVE;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * 프로젝트 생성 시 초기 상태의 지갑을 만든다.
     */
    public static ProjectWallet of(Project project) {
        ProjectWallet wallet = new ProjectWallet();
        wallet.project = project;
        wallet.escrowBalance = 0L;
        wallet.pendingRelease = 0L;
        wallet.releasedTotal = 0L;
        wallet.status = ProjectWalletStatus.ACTIVE;
        wallet.updatedAt = LocalDateTime.now();
        return wallet;
    }

    /**
     * 결제 승인으로 유입된 금액을 에스크로 잔액으로 적립한다.
     */
    public void deposit(long amount) {
        this.escrowBalance += amount;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 환불 시 에스크로 잔액에서 금액을 차감한다.
     */
    public void refund(long amount) {
        if (this.escrowBalance < amount) {
            throw new AppException(
                    ErrorCode.INSUFFICIENT_ESCROW,
                    "에스크로 잔액 부족: 현재=" + this.escrowBalance + ", 요청=" + amount
            );
        }
        this.escrowBalance -= amount;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 정산 생성 시 release 대기 금액을 증가시켜 나중에 지급할 수 있게 표시한다.
     */
    public void holdForRelease(long amount) {
        if (this.escrowBalance < amount) {
            throw new AppException(ErrorCode.INSUFFICIENT_ESCROW);
        }
        this.pendingRelease += amount;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 선지급·잔금 지급 시 실제 에스크로에서 차감하고 release 기록을 남긴다.
     */
    public void release(long amount) {
        if (this.pendingRelease < amount) {
            throw new AppException(ErrorCode.INSUFFICIENT_ESCROW, "대기 금액 부족");
        }
        if (this.escrowBalance < amount) {
            throw new AppException(ErrorCode.INSUFFICIENT_ESCROW, "에스크로 잔액 부족");
        }
        this.pendingRelease -= amount;
        this.escrowBalance -= amount;
        this.releasedTotal += amount;
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
