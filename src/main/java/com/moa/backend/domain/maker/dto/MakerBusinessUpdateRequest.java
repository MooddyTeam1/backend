package com.moa.backend.domain.maker.dto;

import io.swagger.v3.oas.annotations.media.Schema;

// 한글 설명: 메이커 사업자 정보 수정 요청 DTO
@Schema(description = "메이커 사업자 정보 수정 요청")
public record MakerBusinessUpdateRequest(
        @Schema(description = "사업자 상호명", example = "모아 스튜디오")
        String businessName,                 // 한글 설명: 사업자 상호명
        @Schema(description = "사업자등록번호", example = "123-45-67890")
        String businessNumber,               // 한글 설명: 사업자등록번호
        @Schema(description = "업태/업종", example = "제조/서비스")
        String businessItem,                 // 한글 설명: 업태
        @Schema(description = "통신판매업 신고번호", example = "2025-서울강남-1234")
        String onlineSalesRegistrationNo     // 한글 설명: 통신판매업 신고번호
) {
}
