package com.moa.backend.domain.maker.dto;

/**
 * 한글 설명: 메이커 상세 정보 조회 응답 DTO.
 * - /public/makers/{makerId}/info 에서 사용.
 */
public record MakerDetailInfoResponse(
        String coreCompetencies,          // 한글 설명: 핵심 역량
        String establishedAt,             // 한글 설명: 설립일 (yyyy-MM-dd, nullable)
        String industryType,              // 한글 설명: 업종 (nullable)
        String businessItem,              // 한글 설명: 업태 (nullable)
        String businessNumber,            // 한글 설명: 사업자등록번호 (nullable)
        String businessName,              // 한글 설명: 사업자 상호명 (nullable)
        String onlineSalesRegistrationNo, // 한글 설명: 통신판매업 신고번호 (nullable)
        String representative,            // 한글 설명: 대표자명 (nullable)
        String location,                  // 한글 설명: 소재지 (nullable)
        String contactEmail,              // 한글 설명: 연락 이메일 (nullable)
        String contactPhone,              // 한글 설명: 연락 전화번호 (nullable)
        String makerType                  // 한글 설명: 메이커 유형 ("INDIVIDUAL" / "BUSINESS")
) {
}
