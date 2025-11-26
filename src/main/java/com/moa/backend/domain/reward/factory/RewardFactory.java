package com.moa.backend.domain.reward.factory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moa.backend.domain.project.entity.Project;
import com.moa.backend.domain.reward.dto.RewardRequest;
import com.moa.backend.domain.reward.dto.select.OptionGroupRequest;
import com.moa.backend.domain.reward.dto.set.RewardSetRequest;
import com.moa.backend.domain.reward.entity.OptionGroup;
import com.moa.backend.domain.reward.entity.OptionValue;
import com.moa.backend.domain.reward.entity.Reward;
import com.moa.backend.domain.reward.entity.RewardSet;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

/**
 * 한글 설명: RewardRequest DTO를 실제 Reward 엔티티로 변환해주는 팩토리.
 * - 프로젝트 생성/임시저장 시 공통으로 사용된다.
 * - 옵션 그룹, 옵션값, 리워드 세트, 정보고시(disclosure)까지 한 번에 매핑한다.
 */
@Component
@RequiredArgsConstructor
public class RewardFactory {

    // 한글 설명: 정보고시 공통 필드(Map)를 JSON 문자열로 직렬화하는 데 사용
    private final ObjectMapper objectMapper;

    /**
     * 한글 설명:
     * - 하나의 RewardRequest 를 받아 Reward 엔티티를 생성한다.
     * - 기본 정보 + 옵션 그룹 + 세트 + 정보고시(disclosure)를 모두 세팅한다.
     */
    public Reward createReward(Project project, RewardRequest dto) {
        // 1) 기본 리워드 필드 세팅
        Reward reward = Reward.builder()
                .project(project)
                .name(dto.getName())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .stockQuantity(dto.getStockQuantity())
                .estimatedDeliveryDate(dto.getEstimatedDeliveryDate())
                .active(dto.isActive())
                .build();

        // 2) 전자상거래 정보고시 매핑
        if (dto.getDisclosure() != null && dto.getDisclosure().getCategory() != null) {
            // 카테고리(Enum name) 문자열 저장
            reward.setDisclosureCategory(dto.getDisclosure().getCategory().name());

            // 공통 항목 JSON 직렬화
            try {
                if (dto.getDisclosure().getCommon() != null) {
                    String commonJson = objectMapper.writeValueAsString(dto.getDisclosure().getCommon());
                    reward.setDisclosureCommonJson(commonJson);
                } else {
                    reward.setDisclosureCommonJson(null);
                }
            } catch (Exception e) {
                // 한글 설명: 직렬화 실패 시 전체 로직이 죽지 않도록 null 로 처리 (로그는 별도 처리 가능)
                reward.setDisclosureCommonJson(null);
            }

            // 카테고리별 상세 정보 JSON 직렬화
            try {
                if (dto.getDisclosure().getCategorySpecific() != null) {
                    String categorySpecificJson = objectMapper
                            .writeValueAsString(dto.getDisclosure().getCategorySpecific());
                    reward.setDisclosureCategorySpecificJson(categorySpecificJson);
                } else {
                    reward.setDisclosureCategorySpecificJson(null);
                }
            } catch (Exception e) {
                // 한글 설명: 직렬화 실패 시 전체 로직이 죽지 않도록 null 로 처리 (로그는 별도 처리 가능)
                reward.setDisclosureCategorySpecificJson(null);
            }
        }

        // 3) 옵션 그룹 매핑 (직접 옵션이 달린 구조)
        if (!CollectionUtils.isEmpty(dto.getOptionGroups())) {
            for (OptionGroupRequest g : dto.getOptionGroups()) {
                OptionGroup group = toOptionGroup(g);
                // 한글 설명: Reward ↔ OptionGroup 양방향 관계 설정
                reward.addOptionGroup(group);
            }
        }

        // 4) 리워드 세트 매핑 (세트 안에 옵션 그룹/옵션 값이 포함된 구조)
        if (!CollectionUtils.isEmpty(dto.getRewardSets())) {
            for (RewardSetRequest s : dto.getRewardSets()) {
                RewardSet rewardSet = toRewardSet(s);
                // 한글 설명: Reward ↔ RewardSet 양방향 관계 설정
                reward.addRewardSet(rewardSet);
            }
        }

        return reward;
    }

    /**
     * 한글 설명:
     * - OptionGroupRequest DTO 를 실제 OptionGroup 엔티티로 변환한다.
     * - 내부에 포함된 OptionValue 들도 같이 생성해서 연관관계를 맺는다.
     */
    private OptionGroup toOptionGroup(OptionGroupRequest g) {
        OptionGroup group = OptionGroup.builder()
                .groupName(g.getGroupName())
                .build();

        if (!CollectionUtils.isEmpty(g.getOptionValues())) {
            g.getOptionValues().forEach(v -> {
                OptionValue value = OptionValue.builder()
                        .optionValue(v.getOptionValue())
                        .addPrice(v.getAddPrice())
                        .stockQuantity(v.getStockQuantity())
                        .build();
                // 한글 설명: OptionGroup ↔ OptionValue 양방향 관계 설정
                group.addOptionValue(value);
            });
        }
        return group;
    }

    /**
     * 한글 설명:
     * - RewardSetRequest DTO 를 실제 RewardSet 엔티티로 변환한다.
     * - 세트 안에 포함된 OptionGroup 들도 함께 생성하여 연관관계를 맺는다.
     */
    private RewardSet toRewardSet(RewardSetRequest s) {
        RewardSet rewardSet = RewardSet.builder()
                .setName(s.getSetName())
                .stockQuantity(s.getStockQuantity())
                .build();

        if (!CollectionUtils.isEmpty(s.getOptionGroups())) {
            s.getOptionGroups().forEach(g -> rewardSet.addOptionGroup(toOptionGroup(g)));
        }
        return rewardSet;
    }
}
