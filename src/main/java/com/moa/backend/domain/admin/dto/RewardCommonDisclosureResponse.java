package com.moa.backend.domain.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 한글 설명: 리워드 정보고시 공통 항목 DTO.
 * - 전자상거래법에서 요구하는 기본 정보고시 필드들.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RewardCommonDisclosureResponse {

    private String manufacturer;          // 제조자
    private String importer;              // 수입자
    private String countryOfOrigin;       // 제조국/원산지
    private String manufacturingDate;     // 제조연월
    private String releaseDate;           // 출시년월
    private String expirationDate;        // 유통기한
    private String qualityAssurance;      // 품질보증 기준
    private String asContactName;         // A/S 책임자
    private String asContactPhone;       // A/S 전화번호
    private Long shippingFee;             // 배송비
    private Long installationFee;         // 설치비
    private Boolean kcCertification;      // KC 인증 여부
    private String kcCertificationNumber; // KC 인증번호
    private Boolean functionalCertification; // 기능성 인증 여부
    private Boolean importDeclaration;       // 수입신고 여부
}

