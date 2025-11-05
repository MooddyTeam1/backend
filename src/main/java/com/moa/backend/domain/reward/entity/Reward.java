package com.moa.backend.domain.reward.entity;

import com.moa.backend.domain.project.entity.Project;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import com.moa.backend.global.error.AppException;
import com.moa.backend.global.error.ErrorCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "reward")
public class Reward {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "price", nullable = false)
    private Long price;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    @Column(name = "stock_quantity")
    private Integer stockQuantity;

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
}

