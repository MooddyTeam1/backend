package com.moa.backend.domain.maker.dto;

import com.moa.backend.domain.maker.entity.MakerType;

import java.time.LocalDate;
import java.time.LocalDateTime;

// 한글 설명: 메이커 프로필 조회 응답 DTO
public record MakerProfileResponse(
        Long id,
        MakerType makerType,                // 한글 설명: 메이커 유형 (개인 / 사업자)
        String name,                        // 한글 설명: 메이커 이름
        String businessNumber,              // 한글 설명: 사업자등록번호
        String businessName,                // 한글 설명: 사업자 상호명
        LocalDate establishedAt,            // 한글 설명: 설립일
        String industryType,                // 한글 설명: 업종
        String businessItem,                // 한글 설명: 업태
        String onlineSalesRegistrationNo,   // 한글 설명: 통신판매업 신고번호
        String representative,              // 한글 설명: 대표자명
        String location,                    // 한글 설명: 소재지
        String productIntro,                // 한글 설명: 제품/서비스 소개
        String coreCompetencies,            // 한글 설명: 핵심 역량
        String imageUrl,                    // 한글 설명: 브랜드 이미지 URL
        String contactEmail,                // 한글 설명: 이메일
        String contactPhone,                // 한글 설명: 연락처
        String techStackJson,               // 한글 설명: 활용 기술 JSON 문자열
        String keywords,                    // 한글 설명: 키워드 문자열
        LocalDateTime createdAt,
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
