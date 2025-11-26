package com.moa.backend.domain.reward.dto;

import com.moa.backend.domain.reward.entity.RewardDisclosureCategory;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 한글 설명: 프론트에서 리워드 생성/수정 시 서버로 보내는
 * "전자상거래 정보고시" 입력 값 DTO.
 *
 * - 이 DTO는 RewardRequest 안에서 사용된다.
 * - RewardFactory에서 이 값을 Reward 엔티티의 disclosure* 컬럼에 JSON으로 직렬화하여 저장한다.
 * - 프론트엔드 구조: { category, common, categorySpecific }
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RewardDisclosureRequestDTO {

    /**
     * 한글 설명: 정보고시 카테고리 (의류, 식품, 화장품 등)
     * - Enum name() 이 DB에는 문자열로 저장된다.
     * - 필수 필드입니다.
     */
    @NotNull(message = "정보고시 카테고리는 필수입니다.")
    private RewardDisclosureCategory category;

    /**
     * 한글 설명: 공통 정보고시 항목
     * - 모든 카테고리에서 공통으로 사용하는 필드
     * - 필수 필드입니다 (비어있어도 됨).
     */
    @NotNull(message = "공통 정보고시는 필수입니다.")
    @Valid
    private RewardCommonDisclosureRequestDTO common;

    /**
     * 한글 설명: 카테고리별 상세 정보고시 항목
     * - 카테고리에 따라 다른 구조의 필드
     * - 필수 필드입니다 (비어있어도 됨).
     */
    @NotNull(message = "카테고리별 정보고시는 필수입니다.")
    @Valid
    private RewardCategorySpecificDisclosureRequestDTO categorySpecific;
}
