package com.moa.backend.domain.reward.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "reward_sets")
@Builder
public class RewardSet {

    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    //임시 Mock 데이터 때문에 충돌날수 있어서 10번부터 만들어지게 함
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "reward_set_id_seq")
    @jakarta.persistence.SequenceGenerator(name = "reward_set_id_seq", sequenceName = "reward_set_id_seq", initialValue = 10, allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reward_id", nullable = false)
    private Reward reward;

    @Column(name = "set_name")
    private String setName;

    @Column(name = "stock_quantity")
    private Integer stockQuantity;

    @Builder.Default
    @OneToMany(mappedBy = "rewardSet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OptionGroup> optionGroups = new ArrayList<>();

    public void addOptionGroup(OptionGroup group) {
        optionGroups.add(group);
        group.setRewardSet(this);
    }
}
