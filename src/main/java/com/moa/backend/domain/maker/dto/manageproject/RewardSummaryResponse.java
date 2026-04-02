package com.moa.backend.domain.maker.dto.manageproject;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "RewardSummaryResponse DTO")
public class RewardSummaryResponse {

    // 한글 설명: 리워드 ID
    @Schema(description = "id", example = "1")
    private Long id;

    // 한글 설명: 리워드명
    @Schema(description = "title", example = "title")
    private String title;

    // 한글 설명: 리워드 가격 (원)
    @Schema(description = "price", example = "1")
    private Long price;

    // 한글 설명: 판매 수량
    @Schema(description = "salesCount", example = "1")
    private Integer salesCount;

    // 한글 설명: 한정 수량 (null: 무제한)
    @Schema(description = "limitQty", example = "1")
    private Integer limitQty;

    // 한글 설명: 현재 판매 가능 여부
    @Schema(description = "available", example = "true")
    private Boolean available;
}
