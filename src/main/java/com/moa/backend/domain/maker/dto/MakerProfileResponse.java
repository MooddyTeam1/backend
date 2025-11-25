package com.moa.backend.domain.maker.dto;

import com.moa.backend.domain.maker.entity.MakerType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;

// 한글 설명: 메이커 프로필 조회 응답 DTO
@Schema(description = "메이커 프로필 응답")
public record MakerProfileResponse(
        @Schema(description = "메이커 ID", example = "10")
        Long id,
        @Schema(description = "메이커 유형", example = "BUSINESS")
        MakerType makerType,                // 한글 설명: 메이커 유형 (개인 / 사업자)
        @Schema(description = "메이커 이름", example = "모아 스튜디오")
        String name,                        // 한글 설명: 메이커 이름
        @Schema(description = "사업자등록번호", example = "123-45-67890")
        String businessNumber,              // 한글 설명: 사업자등록번호
        @Schema(description = "사업자 상호명", example = "모아 스튜디오")
        String businessName,                // 한글 설명: 사업자 상호명
        @Schema(description = "설립일", example = "2020-01-01")
        LocalDate establishedAt,            // 한글 설명: 설립일
        @Schema(description = "업종", example = "IT 서비스")
        String industryType,                // 한글 설명: 업종
        @Schema(description = "업태", example = "제조/서비스")
        String businessItem,                // 한글 설명: 업태
        @Schema(description = "통신판매업 신고번호", example = "2025-서울강남-1234")
        String onlineSalesRegistrationNo,   // 한글 설명: 통신판매업 신고번호
        @Schema(description = "대표자명", example = "홍길동")
        String representative,              // 한글 설명: 대표자명
        @Schema(description = "소재지", example = "서울특별시 강남구")
        String location,                    // 한글 설명: 소재지
        @Schema(description = "제품/서비스 소개", example = "친환경 텀블러 제조")
        String productIntro,                // 한글 설명: 제품/서비스 소개
        @Schema(description = "핵심 역량", example = "제조/디자인/브랜딩")
        String coreCompetencies,            // 한글 설명: 핵심 역량
        @Schema(description = "브랜드 이미지 URL", example = "https://cdn.moa.com/maker/logo.png")
        String imageUrl,                    // 한글 설명: 브랜드 이미지 URL
        @Schema(description = "이메일", example = "maker@moa.com")
        String contactEmail,                // 한글 설명: 이메일
        @Schema(description = "연락처", example = "02-1234-5678")
        String contactPhone,                // 한글 설명: 연락처
        @Schema(description = "기술 스택 JSON 문자열", example = "[\"React\",\"Spring\"]")
        String techStackJson,               // 한글 설명: 활용 기술 JSON 문자열
        @Schema(description = "키워드", example = "친환경,소셜임팩트,B2B")
        String keywords,                    // 한글 설명: 키워드 문자열
        @Schema(description = "생성 시각", example = "2025-01-01T10:00:00")
        LocalDateTime createdAt,
        @Schema(description = "수정 시각", example = "2025-01-02T10:00:00")
        LocalDateTime updatedAt
) {
    // 한글 설명: 엔티티에서 응답 DTO로 변환할 때 사용하는 팩토리 메서드
    public static MakerProfileResponse of(
            Long id,
            MakerType makerType,
            String name,
            String businessNumber,
            String businessName,
            LocalDate establishedAt,
            String industryType,
            String businessItem,
            String onlineSalesRegistrationNo,
            String representative,
            String location,
            String productIntro,
            String coreCompetencies,
            String imageUrl,
            String contactEmail,
            String contactPhone,
            String techStackJson,
            String keywords,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        return new MakerProfileResponse(
                id,
                makerType,
                name,
                businessNumber,
                businessName,
                establishedAt,
                industryType,
                businessItem,
                onlineSalesRegistrationNo,
                representative,
                location,
                productIntro,
                coreCompetencies,
                imageUrl,
                contactEmail,
                contactPhone,
                techStackJson,
                keywords,
                createdAt,
                updatedAt
        );
    }
}
