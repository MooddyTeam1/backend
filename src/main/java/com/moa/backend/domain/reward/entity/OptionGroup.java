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

    @Builder.Default
    @OneToMany(mappedBy = "optionGroup", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OptionValue> optionValues = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reward_set_id", nullable = true)
    private RewardSet rewardSet;

    public void addOptionValue(OptionValue value) {
        optionValues.add(value);
        value.setOptionGroup(this);
    }
}
