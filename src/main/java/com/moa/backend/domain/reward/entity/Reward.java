package com.moa.backend.domain.reward.entity;

import com.moa.backend.domain.project.entity.Project;
import jakarta.persistence.*;
import com.moa.backend.global.error.AppException;
import com.moa.backend.global.error.ErrorCode;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
    @jakarta.persistence.SequenceGenerator(name = "reward_id_seq", sequenceName = "reward_id_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "price", nullable = false)
    private Long price;

    @Column(name = "estimated_Delivery_Date")
    private LocalDate estimatedDeliveryDate;

    @Column(name = "is_active", nullable = false)   //판매상태
    private boolean active;

    @Column(name = "stock_quantity")        //재고 수량
    private Integer stockQuantity;

    /**
     * 낙관적 락(Optimistic Lock)을 위한 버전 필드.
     * JPA가 UPDATE 시 자동으로 version 값을 확인하고 증가시킨다.
     * 동시에 여러 트랜잭션이 같은 재고를 수정하려 할 때 충돌을 감지한다.
     */
    @Version
    @Column(name = "version")
    private Long version;

    @OneToMany(mappedBy = "reward", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OptionGroup> optionGroups = new ArrayList<>();

    @OneToMany(mappedBy = "reward", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<RewardSet> rewardSets = new ArrayList<>();

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

    public void restoreStock(int quantity) {
        if (this.stockQuantity == null) {
            return;
        }
        if (quantity <= 0) {
            throw new AppException(ErrorCode.VALIDATION_FAILED, "수량은 1 이상이어야 합니다.");
        }
        this.stockQuantity += quantity;
    }

    public void activate(int quantity) {
        if(quantity <=0 )
            this.active=false;
    }

    public void addOptionGroup(OptionGroup optionGroup) {
        optionGroups.add(optionGroup);
        optionGroup.setReward(this);
    }

    public void addRewardSet(RewardSet rewardSet) {
        rewardSets.add(rewardSet);
        rewardSet.setReward(this);
    }
}

