package com.moa.backend.domain.maker.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 한글 설명: 메이커 상세 정보 조회 응답 DTO.
 * - /public/makers/{makerId}/info 에서 사용.
 */
@Schema(description = "메이커 상세 정보 응답")
public record MakerDetailInfoResponse(
        @Schema(description = "핵심 역량", example = "제조/디자인/브랜딩")
        String coreCompetencies,          // 한글 설명: 핵심 역량
        @Schema(description = "설립일(yyyy-MM-dd)", example = "2020-01-01")
        String establishedAt,             // 한글 설명: 설립일 (yyyy-MM-dd, nullable)
        @Schema(description = "업종", example = "IT 서비스")
        String industryType,              // 한글 설명: 업종 (nullable)
        @Schema(description = "업태", example = "제조/서비스")
        String businessItem,              // 한글 설명: 업태 (nullable)
        @Schema(description = "사업자등록번호", example = "123-45-67890")
        String businessNumber,            // 한글 설명: 사업자등록번호 (nullable)
        @Schema(description = "사업자 상호명", example = "모아 스튜디오")
        String businessName,              // 한글 설명: 사업자 상호명 (nullable)
        @Schema(description = "통신판매업 신고번호", example = "2025-서울강남-1234")
        String onlineSalesRegistrationNo, // 한글 설명: 통신판매업 신고번호 (nullable)
        @Schema(description = "대표자명", example = "홍길동")
        String representative,            // 한글 설명: 대표자명 (nullable)
        @Schema(description = "소재지", example = "서울특별시 강남구")
        String location,                  // 한글 설명: 소재지 (nullable)
        @Schema(description = "연락 이메일", example = "maker@moa.com")
        String contactEmail,              // 한글 설명: 연락 이메일 (nullable)
        @Schema(description = "연락 전화번호", example = "02-1234-5678")
        String contactPhone,              // 한글 설명: 연락 전화번호 (nullable)
        @Schema(description = "메이커 유형", example = "BUSINESS")
        String makerType                  // 한글 설명: 메이커 유형 ("INDIVIDUAL" / "BUSINESS")
) {
}
