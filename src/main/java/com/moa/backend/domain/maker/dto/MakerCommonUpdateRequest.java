package com.moa.backend.domain.maker.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

// 한글 설명: 메이커 공통 정보 수정 요청 DTO
@Schema(description = "메이커 공통 정보 수정 요청")
public record MakerCommonUpdateRequest(
        @Schema(description = "메이커 이름", example = "모아 스튜디오")
        String name,                  // 한글 설명: 메이커 이름
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        @Schema(description = "설립일", example = "2020-01-01")
        LocalDate establishedAt,      // 한글 설명: 설립일
        @Schema(description = "업종", example = "IT 서비스")
        String industryType,          // 한글 설명: 업종
        @Schema(description = "대표자명", example = "홍길동")
        String representative,        // 한글 설명: 대표자명
        @Schema(description = "소재지", example = "서울특별시 강남구")
        String location,              // 한글 설명: 소재지
        @Schema(description = "제품/서비스 소개", example = "크라우드펀딩 플랫폼 운영")
        String productIntro,          // 한글 설명: 제품/서비스 소개
        @Schema(description = "핵심 역량", example = "제조/디자인/브랜딩")
        String coreCompetencies,      // 한글 설명: 핵심 역량
        @Schema(description = "브랜드 이미지 URL", example = "https://cdn.moa.com/maker/logo.png")
        String imageUrl,              // 한글 설명: 브랜드 이미지 URL
        @Schema(description = "이메일", example = "maker@moa.com")
        String contactEmail,          // 한글 설명: 이메일
        @Schema(description = "연락처", example = "02-1234-5678")
        String contactPhone,          // 한글 설명: 연락처
        @Schema(description = "활용 기술 JSON 문자열", example = "[\"React\",\"Spring\"]")
        String techStackJson,         // 한글 설명: 활용 기술 JSON 문자열
        @Schema(description = "키워드(콤마 구분)", example = "친환경,소셜임팩트,B2B")
        String keywords               // 한글 설명: 키워드 문자열 (예: 친환경,소셜임팩트,B2B)
) {
}
