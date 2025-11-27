package com.moa.backend.domain.reward.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "reward_option_groups")
@Builder
public class OptionGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "option_group_id_seq")
    @jakarta.persistence.SequenceGenerator(name = "option_group_id_seq", sequenceName = "option_group_id_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reward_id", nullable = true)
    private Reward reward;

    @Column(name = "group_name")
    private String groupName;

    // 한글 설명: @BatchSize를 사용하여 배치 로딩으로 N+1 문제 방지 및 MultipleBagFetchException 회피
    @Builder.Default
    @OneToMany(mappedBy = "optionGroup", cascade = CascadeType.ALL, orphanRemoval = true)
    @org.hibernate.annotations.BatchSize(size = 20)
    private List<OptionValue> optionValues = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reward_set_id", nullable = true)
    private RewardSet rewardSet;

    public void addOptionValue(OptionValue value) {
        optionValues.add(value);
        value.setOptionGroup(this);
    }
}
