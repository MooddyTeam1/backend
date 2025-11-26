package com.moa.backend.domain.reward.dto;

import com.moa.backend.domain.reward.dto.select.OptionGroupResponse;
import com.moa.backend.domain.reward.dto.set.RewardSetResponse;
import com.moa.backend.domain.reward.entity.Reward;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "리워드 응답")
public class RewardResponse {

    // 기본 리워드 정보 -----------------------------
    @Schema(description = "리워드 ID", example = "1001")
    private Long id;
    @Schema(description = "리워드 이름", example = "텀블러 단품")
    private String name;
    @Schema(description = "리워드 설명", example = "350ml 경량 텀블러")
    private String description;
    @Schema(description = "재고 수량", example = "100")
    private Integer stockQuantity;
    @Schema(description = "가격(원)", example = "19000")
    private Long price;
    @Schema(description = "예상 배송일", example = "2025-02-28")
    private LocalDate estimatedDeliveryDate;
    @Schema(description = "판매 활성 여부", example = "true")
    private boolean active = true;

    // 옵션/세트 정보 ------------------------------
    @Schema(description = "옵션 그룹 목록")
    private List<OptionGroupResponse> optionGroups;
    @Schema(description = "리워드 세트 목록")
    private List<RewardSetResponse> rewardSets;

    // 전자상거래 정보고시 응답 --------------------
    @Schema(description = "전자상거래 정보고시")
    private RewardDisclosureResponseDTO disclosure;

    /**
     * 한글 설명: Reward 엔티티를 RewardResponse DTO로 변환하는 팩토리 메서드.
     * - 옵션/세트/정보고시까지 모두 포함해서 매핑한다.
     */
    public static RewardResponse from(Reward reward) {
        return baseBuilder(reward).build();
    }

    public static RewardResponse fromWithDisclosure(Reward reward) {
        return baseBuilder(reward)
                .disclosure(RewardDisclosureResponseDTO.from(reward))
                .build();
    }

    private static RewardResponseBuilder baseBuilder(Reward reward) {
        return RewardResponse.builder()
                .id(reward.getId())
                .name(reward.getName())
                .description(reward.getDescription())
                .stockQuantity(reward.getStockQuantity())
                .price(reward.getPrice())
                .estimatedDeliveryDate(reward.getEstimatedDeliveryDate())
                .active(reward.isActive())
                .rewardSets(reward.getRewardSets() != null
                        ? reward.getRewardSets().stream()
                        .map(RewardSetResponse::from)
                        .toList()
                        : null)
                .optionGroups(reward.getOptionGroups() != null
                        ? reward.getOptionGroups().stream()
                        .map(OptionGroupResponse::from)
                        .toList()
                        : null);
    }

}
