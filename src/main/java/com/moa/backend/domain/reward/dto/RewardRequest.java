package com.moa.backend.domain.reward.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.moa.backend.domain.reward.dto.select.OptionGroupRequest;
import com.moa.backend.domain.reward.dto.set.RewardSetRequest;
import com.moa.backend.global.deserializer.MonthToLocalDateDeserializer;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "리워드 생성/수정 요청")
public class RewardRequest {

    // 한글 설명: 리워드 이름 (프론트엔드에서는 'title'로 보내는 필드를 'name'으로 매핑)
    @Schema(description = "리워드 이름", example = "텀블러 단품")
    @JsonProperty("title")
    private String name;

    // 한글 설명: 리워드 설명
    @Schema(description = "리워드 설명", example = "350ml 경량 텀블러")
    private String description;

    // 한글 설명: 기본 가격 (옵션 추가금 제외)
    @Schema(description = "가격(원)", example = "19000")
    @Positive(message = "가격은 0보다 커야 합니다.")
    private Long price;

    // 한글 설명: 전체 재고 수량 (프론트엔드에서는 'limitQty'로 보내는 필드를 'stockQuantity'로 매핑)
    @Schema(description = "재고 수량", example = "100")
    @Positive(message = "수량은 0보다 커야 합니다.")
    @JsonProperty("limitQty")
    private Integer stockQuantity;

    // 한글 설명: 예상 배송일 (프론트엔드에서는 'estShippingMonth'로 "YYYY-MM" 형식을 보내는 필드를 'estimatedDeliveryDate'로 매핑)
    // 한글 설명: 커스텀 deserializer가 "YYYY-MM" 형식을 해당 월의 첫째 날(LocalDate)로 변환
    @Schema(description = "예상 배송일", example = "2025-02-28")
    @JsonProperty("estShippingMonth")
    @JsonDeserialize(using = MonthToLocalDateDeserializer.class)
    private LocalDate estimatedDeliveryDate;

    // 한글 설명: 기본 활성 여부 (true: 판매중)
    @Schema(description = "판매 활성 여부", example = "true")
    @Builder.Default
    private boolean active = true;

    // 한글 설명: 옵션 그룹 요청 (색상/사이즈 등)
    @Schema(description = "옵션 그룹 목록")
    private List<OptionGroupRequest> optionGroups;

    // 한글 설명: 리워드 세트 요청 (구성/묶음 등)
    @Schema(description = "리워드 세트 목록")
    private List<RewardSetRequest> rewardSets;

    // 한글 설명: 전자상거래 정보고시 입력 값
    // - null 이면 아직 정보고시를 설정하지 않은 상태로 본다.
    @Schema(description = "전자상거래 정보고시")
    private RewardDisclosureRequestDTO disclosure;
}
