package com.moa.backend.domain.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 주문 생성 요청 DTO.
 * 배송지 정보와 리워드 선택 목록을 함께 전달한다.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "주문 생성 요청")
public class OrderCreateRequest {

    // 주문을 진행할 프로젝트 ID
    @Schema(description = "프로젝트 ID", example = "101")
    @NotNull
    private Long projectId;

    // 수령인 이름
    @Schema(description = "수령인 이름", example = "홍길동")
    @NotBlank
    private String receiverName;

    // 수령인 연락처
    @Schema(description = "수령인 연락처", example = "010-1234-5678")
    @NotBlank
    private String receiverPhone;

    // 기본 주소
    @Schema(description = "기본 주소", example = "서울특별시 강남구 테헤란로 1")
    @NotBlank
    private String addressLine1;

    // 상세 주소
    @Schema(description = "상세 주소", example = "101동 202호")
    private String addressLine2;

    // 우편번호
    @Schema(description = "우편번호", example = "06234")
    @NotBlank
    private String zipCode;

    // 구매할 리워드 구성
    @Schema(description = "구매할 리워드 목록")
    @Valid
    @NotEmpty
    @Builder.Default
    private List<Item> items = new ArrayList<>();

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "주문 항목")
    public static class Item {

        // 선택한 리워드 ID
        @Schema(description = "리워드 ID", example = "1001")
        @NotNull
        private Long rewardId;

        // 수량 (1 이상)
        @Schema(description = "주문 수량(1 이상)", example = "2")
        @NotNull
        @Positive
        private Integer quantity;

        // 옵션 / 메모
        @Schema(description = "옵션/메모", example = "블랙 색상, 사인 요청")
        private String note;
    }
}
