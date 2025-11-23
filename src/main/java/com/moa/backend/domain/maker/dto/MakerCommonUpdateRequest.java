package com.moa.backend.domain.maker.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

// 한글 설명: 메이커 공통 정보 수정 요청 DTO
public record MakerCommonUpdateRequest(
        String name,                  // 한글 설명: 메이커 이름
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        LocalDate establishedAt,      // 한글 설명: 설립일
        String industryType,          // 한글 설명: 업종
        String representative,        // 한글 설명: 대표자명
        String location,              // 한글 설명: 소재지
        String productIntro,          // 한글 설명: 제품/서비스 소개
        String coreCompetencies,      // 한글 설명: 핵심 역량
        String imageUrl,              // 한글 설명: 브랜드 이미지 URL
        String contactEmail,          // 한글 설명: 이메일
        String contactPhone,          // 한글 설명: 연락처
        String techStackJson,         // 한글 설명: 활용 기술 JSON 문자열
        String keywords               // 한글 설명: 키워드 문자열 (예: 친환경,소셜임팩트,B2B)
) {
}
