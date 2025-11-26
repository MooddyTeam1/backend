package com.moa.backend.domain.maker.dto.manageproject;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 한글 설명: 프로젝트 내 리워드 요약 정보 DTO.
 * - 메이커 관리 화면의 리워드 리스트 영역에 사용.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RewardSummaryResponse {

    // 한글 설명: 리워드 ID
    private Long id;

    // 한글 설명: 리워드명
    private String title;

    // 한글 설명: 리워드 가격 (원)
    private Long price;

    // 한글 설명: 판매 수량
    private Integer salesCount;

    // 한글 설명: 한정 수량 (null: 무제한)
    private Integer limitQty;

    // 한글 설명: 현재 판매 가능 여부
    private Boolean available;
}
