package com.moa.backend.domain.maker.dto;

import com.moa.backend.domain.maker.entity.Maker;
import com.moa.backend.domain.maker.entity.MakerType;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "메이커 공개 프로필 응답")
public class MakerPublicProfileResponse {

    // 기본 식별자
    @Schema(description = "메이커 ID", example = "310")
    private Long id;
    @Schema(description = "메이커 유형 (INDIVIDUAL, BUSINESS)", example = "BUSINESS")
    private MakerType makerType;

    // 이름/브랜드/사업자 정보
    @Schema(description = "메이커명", example = "그린랩 스튜디오")
    private String name;                    // 메이커명(브랜드/스튜디오명)
    @Schema(description = "사업자 상호명", example = "그린랩 주식회사")
    private String businessName;            // 사업자 상호명
    @Schema(description = "사업자등록번호", example = "123-45-67890")
    private String businessNumber;          // 사업자등록번호
    @Schema(description = "대표자명", example = "김진현")
    private String representative;          // 대표자명
    @Schema(description = "설립일", example = "2021-03-15")
    private LocalDate establishedAt;        // 설립일
    @Schema(description = "업종", example = "친환경 생활용품 제조")
    private String industryType;            // 업종 (예: 스마트 하드웨어)
    @Schema(description = "업태", example = "제조업")
    private String businessItem;            // 업태 (예: 제조업, 도매 및 소매업)
    @Schema(description = "통신판매업 신고번호", example = "2025-서울강남-1234")
    private String onlineSalesRegistrationNo; // 통신판매업 신고번호

    // 프로필/소개 관련
    @Schema(description = "소재지", example = "서울특별시 강남구 테헤란로 123")
    private String location;                // 소재지
    @Schema(description = "제품/서비스 소개", example = "일상에서 오래 쓰는 친환경 제품을 만듭니다.")
    private String productIntro;            // 제품/서비스 소개
    @Schema(description = "핵심 역량", example = "친환경 소재 설계, 소량 다품종 생산")
    private String coreCompetencies;        // 핵심 역량
    @Schema(description = "프로필 이미지 URL", example = "https://cdn.moa.com/makers/310/profile.jpg")
    private String imageUrl;                // 브랜드 이미지 URL

    // 연락처
    @Schema(description = "문의 이메일", example = "hello@greenlab.co.kr")
    private String contactEmail;
    @Schema(description = "문의 전화번호", example = "02-1234-5678")
    private String contactPhone;

    // 태그/기술 스택
    @Schema(description = "기술/역량 태그 JSON 문자열", example = "[\"친환경소재\",\"제품디자인\"]")
    private String techStackJson;           // JSON 문자열 (["React","Node.js"] 등)
    @Schema(description = "검색 키워드(쉼표 구분)", example = "친환경,텀블러,리빙")
    private String keywords;                // 쉼표 구분 키워드

    // 생성/수정 시각
    @Schema(description = "생성 시각", example = "2025-01-10T10:00:00")
    private LocalDateTime createdAt;
    @Schema(description = "수정 시각", example = "2025-11-02T15:45:10")
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
