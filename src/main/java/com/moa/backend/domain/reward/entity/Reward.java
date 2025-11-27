package com.moa.backend.domain.reward.entity;

import com.moa.backend.domain.project.entity.Project;
import com.moa.backend.global.error.AppException;
import com.moa.backend.global.error.ErrorCode;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 한글 설명: 프로젝트에 종속되는 리워드(후원 상품/구성) 엔티티.
 * - 기본 정보(이름, 설명, 가격, 재고, 예상 배송일)
 * - 옵션 그룹/세트 구조
 * - 전자상거래 정보고시(disclosure*) 필드를 함께 관리한다.
 */
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Table(name = "rewards")
@Builder
public class Reward {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "reward_id_seq")
    @SequenceGenerator(name = "reward_id_seq", sequenceName = "reward_id_seq", allocationSize = 1)
    private Long id;

    // 한글 설명: 이 리워드가 속한 프로젝트 정보 (N:1 관계)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    // 한글 설명: 리워드 이름
    @Column(name = "name", nullable = false, length = 200)
    private String name;

    // 한글 설명: 리워드 상세 설명
    @Column(name = "description", nullable = false)
    private String description;

    // 한글 설명: 기본 리워드 가격 (옵션 추가금 제외)
    @Column(name = "price", nullable = false)
    private Long price;

    // 한글 설명: 예상 배송일 (날짜만)
    @Column(name = "estimated_Delivery_Date")
    private LocalDate estimatedDeliveryDate;

    // 한글 설명: 리워드 판매 상태 (true: 판매중, false: 비활성)
    @Column(name = "is_active", nullable = false)
    private boolean active;

    // 한글 설명: 리워드 전체 재고 수량 (옵션 재고와 별도로 운영 가능)
    @Column(name = "stock_quantity")
    private Integer stockQuantity;

    // ===================== 전자상거래 정보고시 필드 =====================

    /**
     * 한글 설명: 정보고시 카테고리 (Enum 문자열)
     * - RewardDisclosureCategory.name() 값이 저장된다.
     * - null 이면 정보고시가 아직 설정되지 않은 상태로 본다.
     */
    @Column(name = "disclosure_category", length = 50)
    private String disclosureCategory;

    /**
     * 한글 설명: 공통 정보고시 항목(JSON 문자열)
     * - RewardDisclosureRequestDTO.toCommonMap() 을 JSON 으로 직렬화하여 저장.
     */
    @Column(name = "disclosure_common_json", columnDefinition = "TEXT")
    private String disclosureCommonJson;

    /**
     * 한글 설명: 카테고리별 상세 정보(JSON 문자열)
     * - 카테고리별로 자유롭게 정의한 JSON 문자열을 그대로 저장한다.
     */
    @Column(name = "disclosure_category_specific_json", columnDefinition = "TEXT")
    private String disclosureCategorySpecificJson;

    // ===================== 재고/락 필드 =====================

    /**
     * 한글 설명: 낙관적 락(Optimistic Lock)을 위한 버전 필드.
     * JPA가 UPDATE 시 자동으로 version 값을 확인하고 증가시킨다.
     * 동시에 여러 트랜잭션이 같은 재고를 수정하려 할 때 충돌을 감지한다.
     */
    @Version
    @Column(name = "version")
    private Long version;

    // ===================== 연관관계 =====================

    // 한글 설명: 리워드 직속 옵션 그룹 목록 (색상/사이즈 등)
    // 한글 설명: @BatchSize를 사용하여 배치 로딩으로 N+1 문제 방지 및 MultipleBagFetchException 회피
    @OneToMany(mappedBy = "reward", cascade = CascadeType.ALL, orphanRemoval = true)
    @org.hibernate.annotations.BatchSize(size = 20)
    @Builder.Default
    private List<OptionGroup> optionGroups = new ArrayList<>();

    // 한글 설명: 리워드 세트(세트 구성, 묶음 상품 등)
    @OneToMany(mappedBy = "reward", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<RewardSet> rewardSets = new ArrayList<>();

    // ===================== 비즈니스 메서드 =====================

    // 한글 설명: 재고 차감 (결제/주문 시 사용)
    public void decreaseStock(int quantity) {
        if (this.stockQuantity == null) {
            return;
        }
        if (quantity <= 0) {
            throw new AppException(ErrorCode.VALIDATION_FAILED, "수량은 1 이상이어야 합니다.");
        }
        if (this.stockQuantity < quantity) {
            throw new AppException(ErrorCode.BUSINESS_CONFLICT, "리워드 재고가 부족합니다.");
        }
        this.stockQuantity -= quantity;
    }

    // 한글 설명: 재고 복구 (주문 취소/환불 시 사용)
    public void restoreStock(int quantity) {
        if (this.stockQuantity == null) {
            return;
        }
        if (quantity <= 0) {
            throw new AppException(ErrorCode.VALIDATION_FAILED, "수량은 1 이상이어야 합니다.");
        }
        this.stockQuantity += quantity;
    }

    // 한글 설명: 활성/비활성 처리 (수량이 0 이하이면 비활성화)
    public void activate(int quantity) {
        if (quantity <= 0) {
            this.active = false;
        } else {
            this.active = true;
        }
    }

    // 한글 설명: 옵션 그룹 추가 (양방향 연관관계 편의 메서드)
    public void addOptionGroup(OptionGroup optionGroup) {
        optionGroups.add(optionGroup);
        optionGroup.setReward(this);
    }

    // 한글 설명: 리워드 세트 추가 (양방향 연관관계 편의 메서드)
    public void addRewardSet(RewardSet rewardSet) {
        rewardSets.add(rewardSet);
        rewardSet.setReward(this);
    }

    // 한글 설명: 재고 수량 증가 (메이커 관리용, 운송장 오입력 등 보정)
    public void increaseStock(int quantity) {
        if (quantity <= 0) {
            throw new AppException(ErrorCode.VALIDATION_FAILED, "추가 수량은 1 이상이어야 합니다.");
        }

        if (this.stockQuantity == null) {
            this.stockQuantity = quantity;
        } else {
            this.stockQuantity += quantity;
        }
    }
}
