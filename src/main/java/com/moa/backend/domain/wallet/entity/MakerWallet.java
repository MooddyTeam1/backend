package com.moa.backend.domain.wallet.entity;

import com.moa.backend.domain.maker.entity.Maker;
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
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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

    @Column(name = "available_balance", nullable = false)
    private Long availableBalance = 0L;

    @Column(name = "pending_balance", nullable = false)
    private Long pendingBalance = 0L;

    @Column(name = "total_earned", nullable = false)
    private Long totalEarned = 0L;

    @Column(name = "total_withdrawn", nullable = false)
    private Long totalWithdrawn = 0L;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
