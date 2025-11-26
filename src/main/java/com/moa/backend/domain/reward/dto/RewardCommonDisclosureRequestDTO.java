package com.moa.backend.domain.reward.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 한글 설명: 리워드 정보고시 공통 필드 DTO.
 * - 모든 카테고리에서 공통으로 사용하는 정보고시 항목.
 * - 모든 필드는 선택적(Optional)입니다.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RewardCommonDisclosureRequestDTO {

    // 한글 설명: 제조자 (수입품인 경우 수입자 포함)
    private String manufacturer;

    // 한글 설명: 수입자 (수입품인 경우)
    private String importer;

    // 한글 설명: 제조국(원산지)
    private String countryOfOrigin;

    // 한글 설명: 제조연월
    private String manufacturingDate;

    // 한글 설명: 출시년월
    private String releaseDate;

    // 한글 설명: 유통기한
    private String expirationDate;

    // 한글 설명: 품질보증 기준
    private String qualityAssurance;

    // 한글 설명: A/S 책임자 이름 또는 업체명
    private String asContactName;

    // 한글 설명: A/S 전화번호
    private String asContactPhone;

    // 한글 설명: 배송비
    private Long shippingFee;

    // 한글 설명: 설치비
    private Long installationFee;

    // 한글 설명: KC 인증 여부
    private Boolean kcCertification;

    // 한글 설명: KC 인증번호
    private String kcCertificationNumber;

    // 한글 설명: 기능성 인증
    private Boolean functionalCertification;

    // 한글 설명: 수입신고 여부
    private Boolean importDeclaration;
}
