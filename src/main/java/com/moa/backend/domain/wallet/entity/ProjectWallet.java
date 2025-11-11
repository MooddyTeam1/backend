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

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "project_wallets")
public class ProjectWallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false, unique = true)
    private Project project;

    @Column(name = "escrow_balance", nullable = false)
    private Long escrowBalance = 0L;

    @Column(name = "pending_release", nullable = false)
    private Long pendingRelease = 0L;

    @Column(name = "released_total", nullable = false)
    private Long releasedTotal = 0L;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ProjectWalletStatus status = ProjectWalletStatus.ACTIVE;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

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

    public void deposit(long amount) {
        this.escrowBalance += amount;
        this.updatedAt = LocalDateTime.now();
    }

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

    public void holdForRelease(long amount) {
        if (this.escrowBalance < amount) {
            throw new AppException(ErrorCode.INSUFFICIENT_ESCROW);
        }
        this.pendingRelease += amount;
        this.updatedAt = LocalDateTime.now();
    }

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
