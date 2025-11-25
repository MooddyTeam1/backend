package com.moa.backend.domain.maker.dto;

import com.moa.backend.domain.maker.entity.Maker;
import com.moa.backend.domain.maker.entity.MakerType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 한글 설명: 메이커 공개 프로필 응답 DTO
 * - 메이커 홈 공개 페이지(/public/makers/{makerId})에서 사용하는 기본 정보.
 * - 민감한 정보(내부용 정산 계좌 등)는 포함하지 않는다.
 */
@Getter
@Builder
public class MakerPublicProfileResponse {

    // 기본 식별자
    private Long id;
    private MakerType makerType;

    // 이름/브랜드/사업자 정보
    private String name;                    // 메이커명(브랜드/스튜디오명)
    private String businessName;            // 사업자 상호명
    private String businessNumber;          // 사업자등록번호
    private String representative;          // 대표자명
    private LocalDate establishedAt;        // 설립일
    private String industryType;            // 업종 (예: 스마트 하드웨어)
    private String businessItem;            // 업태 (예: 제조업, 도매 및 소매업)
    private String onlineSalesRegistrationNo; // 통신판매업 신고번호

    // 프로필/소개 관련
    private String location;                // 소재지
    private String productIntro;            // 제품/서비스 소개
    private String coreCompetencies;        // 핵심 역량
    private String imageUrl;                // 브랜드 이미지 URL

    // 연락처
    private String contactEmail;
    private String contactPhone;

    // 태그/기술 스택
    private String techStackJson;           // JSON 문자열 (["React","Node.js"] 등)
    private String keywords;                // 쉼표 구분 키워드

    // 생성/수정 시각
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 한글 설명: Maker 엔티티 -> 공개용 DTO 변환 헬퍼 메서드
    public static MakerPublicProfileResponse from(Maker maker) {
        return MakerPublicProfileResponse.builder()
                .id(maker.getId())
                .makerType(maker.getMakerType())
                .name(maker.getName())
                .businessName(maker.getBusinessName())
                .businessNumber(maker.getBusinessNumber())
                .representative(maker.getRepresentative())
                .establishedAt(maker.getEstablishedAt())
                .industryType(maker.getIndustryType())
                .businessItem(maker.getBusinessItem())
                .onlineSalesRegistrationNo(maker.getOnlineSalesRegistrationNo())
                .location(maker.getLocation())
                .productIntro(maker.getProductIntro())
                .coreCompetencies(maker.getCoreCompetencies())
                .imageUrl(maker.getImageUrl())
                .contactEmail(maker.getContactEmail())
                .contactPhone(maker.getContactPhone())
                .techStackJson(maker.getTechStackJson())
                .keywords(maker.getKeywords())
                .createdAt(maker.getCreatedAt())
                .updatedAt(maker.getUpdatedAt())
                .build();
    }
}
