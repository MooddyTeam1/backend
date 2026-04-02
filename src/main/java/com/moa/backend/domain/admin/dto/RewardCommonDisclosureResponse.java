package com.moa.backend.domain.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "RewardCommonDisclosureResponse DTO")
public class RewardCommonDisclosureResponse {

    @Schema(description = "manufacturer", example = "manufacturer")

    private String manufacturer;          // 제조자
    @Schema(description = "importer", example = "importer")
    private String importer;              // 수입자
    @Schema(description = "countryOfOrigin", example = "countryOfOrigin")
    private String countryOfOrigin;       // 제조국/원산지
    @Schema(description = "manufacturingDate", example = "manufacturingDate")
    private String manufacturingDate;     // 제조연월
    @Schema(description = "releaseDate", example = "releaseDate")
    private String releaseDate;           // 출시년월
    @Schema(description = "expirationDate", example = "expirationDate")
    private String expirationDate;        // 유통기한
    @Schema(description = "qualityAssurance", example = "qualityAssurance")
    private String qualityAssurance;      // 품질보증 기준
    @Schema(description = "asContactName", example = "asContactName")
    private String asContactName;         // A/S 책임자
    @Schema(description = "asContactPhone", example = "asContactPhone")
    private String asContactPhone;       // A/S 전화번호
    @Schema(description = "shippingFee", example = "1")
    private Long shippingFee;             // 배송비
    @Schema(description = "installationFee", example = "1")
    private Long installationFee;         // 설치비
    @Schema(description = "kcCertification", example = "true")
    private Boolean kcCertification;      // KC 인증 여부
    @Schema(description = "kcCertificationNumber", example = "kcCertificationNumber")
    private String kcCertificationNumber; // KC 인증번호
    @Schema(description = "functionalCertification", example = "true")
    private Boolean functionalCertification; // 기능성 인증 여부
    @Schema(description = "importDeclaration", example = "true")
    private Boolean importDeclaration;       // 수입신고 여부
}

