package com.moa.backend.domain.maker.entity;

import com.moa.backend.domain.maker.entity.Maker;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 한글 설명: 메이커의 정산 계좌 정보를 보관하는 엔티티.
 * - Maker 와 1:1 관계 (한 메이커당 하나의 정산 계좌)
 * - createdAt, updatedAt 을 엔티티 내부 @PrePersist/@PreUpdate 로 직접 관리.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "maker_settlement_profiles")
public class MakerSettlementProfile {

    /**
     * 한글 설명: 정산 계좌 정보 PK (자동 증가).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 한글 설명: 정산 계좌 소유 메이커 (1:1 관계).
     * - maker_id 컬럼으로 makers.id 를 참조.
     * - unique = true 로 메이커당 하나의 정산 계좌만 허용.
     */
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "maker_id", nullable = false, unique = true)
    private Maker maker;

    /**
     * 한글 설명: 은행명 (예: "KB국민은행", "신한은행").
     */
    @Column(name = "bank_name", length = 50, nullable = false)
    private String bankName;

    /**
     * 한글 설명: 계좌번호 (예: "123-456-789012").
     * - 실제 운영 시에는 암호화 저장도 고려.
     */
    @Column(name = "account_number", length = 50, nullable = false)
    private String accountNumber;

    /**
     * 한글 설명: 예금주명 (예: "홍길동", "주식회사 테크").
     */
    @Column(name = "account_holder", length = 100, nullable = false)
    private String accountHolder;

    /**
     * 한글 설명: 레코드 생성 시각.
     * - @PrePersist 에서 자동 세팅.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 한글 설명: 레코드 마지막 수정 시각.
     * - @PrePersist, @PreUpdate 에서 자동 세팅.
     */
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * 한글 설명: 정산 계좌 신규 등록에 사용하는 빌더 생성자.
     */
    @Builder
    private MakerSettlementProfile(
            Maker maker,
            String bankName,
            String accountNumber,
            String accountHolder
    ) {
        this.maker = maker;
        this.bankName = bankName;
        this.accountNumber = accountNumber;
        this.accountHolder = accountHolder;
    }

    // ==================== JPA 라이프사이클 콜백 ====================

    /**
     * 한글 설명: INSERT 전에 생성/수정 시간을 현재 시각으로 세팅.
     */
    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    /**
     * 한글 설명: UPDATE 전에 수정 시간을 현재 시각으로 갱신.
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // ==================== 변경 메서드(수정용) ====================

    /**
     * 한글 설명: 은행명 변경.
     */
    public void updateBankName(String bankName) {
        this.bankName = bankName;
    }

    /**
     * 한글 설명: 계좌번호 변경.
     */
    public void updateAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    /**
     * 한글 설명: 예금주명 변경.
     */
    public void updateAccountHolder(String accountHolder) {
        this.accountHolder = accountHolder;
    }
}
