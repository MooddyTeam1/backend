package com.moa.backend.domain.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moa.backend.domain.maker.entity.Maker;
import com.moa.backend.domain.maker.entity.MakerType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * 한글 설명: 관리자용 메이커 프로필 DTO.
 * - makerType(INDIVIDUAL/BUSINESS)에 따라 일부 필드는 null 일 수 있다.
 * - 개인/사업자 공통 필드 + 선택 필드 한 DTO로 통합.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "AdminMakerProfileResponse DTO")
public class AdminMakerProfileResponse {

    // ================= 공통 필드 =================

    @Schema(description = "id", example = "1")

    private Long id;                 // 메이커 ID
    @Schema(description = "ownerUserId", example = "1")
    private Long ownerUserId;        // 메이커 소유자 유저 ID
    @Schema(description = "makerType (허용값은 서버 enum 정의 참조)", example = "makerType")
    private MakerType makerType;     // INDIVIDUAL / BUSINESS
    @Schema(description = "name", example = "name")
    private String name;             // 메이커 이름(개인명 or 상호명)

    @Schema(description = "productIntro", example = "productIntro")

    private String productIntro;     // 제품/브랜드 소개
    @Schema(description = "coreCompetencies", example = "coreCompetencies")
    private String coreCompetencies; // 핵심 역량
    @Schema(description = "contactEmail", example = "contactEmail")
    private String contactEmail;     // 연락 이메일
    @Schema(description = "contactPhone", example = "contactPhone")
    private String contactPhone;     // 연락 전화번호

    @Schema(description = "createdAt", example = "2025-11-01T10:00:00")

    private LocalDateTime createdAt;
    @Schema(description = "updatedAt", example = "2025-11-01T10:00:00")
    private LocalDateTime updatedAt;

    // ================= 개인 메이커 전용 필드 =================

    @Schema(description = "imageUrl", example = "imageUrl")

    private String imageUrl;         // 프로필 이미지
    @Schema(description = "techStack", example = "[]")
    private List<String> techStack;  // 기술 스택
    @Schema(description = "keywords", example = "[]")
    private List<String> keywords;  // 관심/키워드 목록 (쉼표 구분 문자열을 파싱)

    // ================= 사업자 메이커 전용 필드 =================

    @Schema(description = "businessNumber", example = "businessNumber")

    private String businessNumber;           // 사업자번호
    @Schema(description = "businessName", example = "businessName")
    private String businessName;             // 사업자명
    @Schema(description = "businessItem", example = "businessItem")
    private String businessItem;             // 업태/업종 요약 (예: "제조업, 도매 및 소매업")
    @Schema(description = "onlineSalesReportNumber", example = "onlineSalesReportNumber")
    private String onlineSalesReportNumber;  // 통신판매업 신고번호
    @Schema(description = "establishedAt", example = "2025-11-01")
    private LocalDate establishedAt;         // 설립일
    @Schema(description = "industryType", example = "industryType")
    private String industryType;             // 업종 (예: 전자제품 제조업)
    @Schema(description = "representative", example = "representative")
    private String representative;           // 대표자명
    @Schema(description = "location", example = "location")
    private String location;                 // 소재지 주소

    /**
     * 한글 설명: Maker 엔티티에서 관리자용 프로필 DTO로 변환하는 정적 메서드.
     * - 실제 필드명/타입은 Maker 엔티티에 맞춰 수정됨.
     */
    public static AdminMakerProfileResponse from(Maker maker) {
        // 한글 설명: maker가 null이거나 owner가 null인 경우 예외 발생
        if (maker == null) {
            throw new IllegalArgumentException("Maker는 null일 수 없습니다.");
        }
        if (maker.getOwner() == null) {
            throw new IllegalStateException("Maker의 owner 정보가 없습니다. makerId=" + maker.getId());
        }

        ObjectMapper objectMapper = new ObjectMapper();

        // 한글 설명: techStackJson을 List<String>으로 파싱
        List<String> techStackList = Collections.emptyList();
        if (maker.getTechStackJson() != null && !maker.getTechStackJson().isBlank()) {
            try {
                techStackList = objectMapper.readValue(
                        maker.getTechStackJson(),
                        new TypeReference<List<String>>() {}
                );
            } catch (Exception e) {
                // 파싱 실패 시 빈 리스트
                techStackList = Collections.emptyList();
            }
        }

        // 한글 설명: keywords는 쉼표 구분 문자열이므로 List로 변환
        List<String> keywordsList = Collections.emptyList();
        if (maker.getKeywords() != null && !maker.getKeywords().isBlank()) {
            keywordsList = List.of(maker.getKeywords().split(","));
        }

        return AdminMakerProfileResponse.builder()
                .id(maker.getId())
                .ownerUserId(maker.getOwner().getId())
                .makerType(maker.getMakerType())
                .name(maker.getName())
                .productIntro(maker.getProductIntro())
                .coreCompetencies(maker.getCoreCompetencies())
                .contactEmail(maker.getContactEmail())
                .contactPhone(maker.getContactPhone())
                .createdAt(maker.getCreatedAt())
                .updatedAt(maker.getUpdatedAt())
                // 개인 메이커 필드 (INDIVIDUAL일 때 주로 사용)
                .imageUrl(maker.getImageUrl())
                .techStack(techStackList)
                .keywords(keywordsList)
                // 사업자 메이커 필드 (BUSINESS일 때 주로 사용)
                .businessNumber(maker.getBusinessNumber())
                .businessName(maker.getBusinessName())
                .businessItem(maker.getBusinessItem())     // 업태/업종 요약
                .onlineSalesReportNumber(maker.getOnlineSalesRegistrationNo())
                .establishedAt(maker.getEstablishedAt())
                .industryType(maker.getIndustryType())
                .representative(maker.getRepresentative())
                .location(maker.getLocation())
                .build();
    }
}

