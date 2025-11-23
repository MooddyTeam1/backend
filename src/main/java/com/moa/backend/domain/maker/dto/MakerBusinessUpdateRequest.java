package com.moa.backend.domain.maker.dto;

// 한글 설명: 메이커 사업자 정보 수정 요청 DTO
public record MakerBusinessUpdateRequest(
        String businessName,                 // 한글 설명: 사업자 상호명
        String businessNumber,               // 한글 설명: 사업자등록번호
        String businessItem,                 // 한글 설명: 업태
        String onlineSalesRegistrationNo     // 한글 설명: 통신판매업 신고번호
) {
}
