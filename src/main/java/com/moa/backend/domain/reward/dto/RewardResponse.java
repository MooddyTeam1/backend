package com.moa.backend.domain.reward.dto;

import com.moa.backend.domain.reward.dto.select.OptionGroupResponse;
import com.moa.backend.domain.reward.dto.set.RewardSetResponse;
import com.moa.backend.domain.reward.entity.Reward;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 한글 설명: 서버에서 클라이언트로 내려주는 리워드 응답 DTO.
 * - 프로젝트 임시저장/조회, 프로젝트 상세조회 등에 사용된다.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RewardResponse {

    // 기본 리워드 정보 -----------------------------
    private Long id;
    private String name;
    private String description;
    private Integer stockQuantity;
    private Long price;
    private LocalDate estimatedDeliveryDate;
    private boolean active = true;

    // 옵션/세트 정보 ------------------------------
    private List<OptionGroupResponse> optionGroups;
    private List<RewardSetResponse> rewardSets;

    // 전자상거래 정보고시 응답 --------------------
    private RewardDisclosureResponseDTO disclosure;

    /**
     * 한글 설명: Reward 엔티티를 RewardResponse DTO로 변환하는 팩토리 메서드.
     * - 옵션/세트/정보고시까지 모두 포함해서 매핑한다.
     */
    public static RewardResponse from(Reward reward) {
        return RewardResponse.builder()
                .id(reward.getId())
                .name(reward.getName())
                .description(reward.getDescription())
                .stockQuantity(reward.getStockQuantity())
                .price(reward.getPrice())
                .estimatedDeliveryDate(reward.getEstimatedDeliveryDate())
                .active(reward.isActive())
                // 세트 응답 매핑
                .rewardSets(reward.getRewardSets() != null
                        ? reward.getRewardSets().stream()
                        .map(RewardSetResponse::from)
                        .toList()
                        : null)
                // 옵션 그룹 응답 매핑
                .optionGroups(reward.getOptionGroups() != null
                        ? reward.getOptionGroups().stream()
                        .map(OptionGroupResponse::from)
                        .toList()
                        : null)
                // 정보고시 응답 매핑
                .disclosure(RewardDisclosureResponseDTO.from(reward))
                .build();
    }
}
