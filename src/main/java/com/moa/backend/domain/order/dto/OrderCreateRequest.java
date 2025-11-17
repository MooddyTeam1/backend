package com.moa.backend.domain.order.dto;

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
public class OrderCreateRequest {

    // 주문을 진행할 프로젝트 ID
    @NotNull
    private Long projectId;

    // 수령인 이름
    @NotBlank
    private String receiverName;

    // 수령인 연락처
    @NotBlank
    private String receiverPhone;

    // 기본 주소
    @NotBlank
    private String addressLine1;

    // 상세 주소
    private String addressLine2;

    // 우편번호
    @NotBlank
    private String zipCode;

    // 구매할 리워드 구성
    @Valid
    @NotEmpty
    @Builder.Default
    private List<Item> items = new ArrayList<>();

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Item {

        // 선택한 리워드 ID
        @NotNull
        private Long rewardId;

        // 수량 (1 이상)
        @NotNull
        @Positive
        private Integer quantity;

        // 옵션 / 메모
        private String note;
    }
}
