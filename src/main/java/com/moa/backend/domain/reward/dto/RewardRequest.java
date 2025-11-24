package com.moa.backend.domain.reward.dto;

import com.moa.backend.domain.reward.dto.select.OptionGroupRequest;
import com.moa.backend.domain.reward.dto.set.RewardSetRequest;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 한글 설명: 프로젝트 생성/임시저장 시
 * 프론트에서 넘어오는 리워드 생성/수정 요청 DTO.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RewardRequest {

    // 한글 설명: 리워드 이름
    private String name;

    // 한글 설명: 리워드 설명
    private String description;

    // 한글 설명: 기본 가격 (옵션 추가금 제외)
    @Positive(message = "가격은 0보다 커야 합니다.")
    private Long price;

    // 한글 설명: 전체 재고 수량
    @Positive(message = "수량은 0보다 커야 합니다.")
    private Integer stockQuantity;

    // 한글 설명: 예상 배송일
    private LocalDate estimatedDeliveryDate;

    // 한글 설명: 기본 활성 여부 (true: 판매중)
    @Builder.Default
    private boolean active = true;

    // 한글 설명: 옵션 그룹 요청 (색상/사이즈 등)
    private List<OptionGroupRequest> optionGroups;

    // 한글 설명: 리워드 세트 요청 (구성/묶음 등)
    private List<RewardSetRequest> rewardSets;

    // 한글 설명: 전자상거래 정보고시 입력 값
    // - null 이면 아직 정보고시를 설정하지 않은 상태로 본다.
    private RewardDisclosureRequestDTO disclosure;
}
