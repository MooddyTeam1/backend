package com.moa.backend.domain.reward.entity;

import com.moa.backend.global.error.AppException;
import com.moa.backend.global.error.ErrorCode;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "reward_option_values")
@Builder
public class OptionValue {

    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    //임시 Mock 데이터 때문에 충돌날수 있어서 10번부터 만들어지게 함
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "option_value_id_seq")
    @jakarta.persistence.SequenceGenerator(name = "option_value_id_seq", sequenceName = "option_value_id_seq", initialValue = 10, allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reward_option_group_id", nullable = false)
    private OptionGroup optionGroup;

    @Column(name = "option_value")
    private String optionValue;

    @Column(name = "add_price")
    private Long addPrice;

    @Column(name = "stock_quantity")        //재고 수량
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
