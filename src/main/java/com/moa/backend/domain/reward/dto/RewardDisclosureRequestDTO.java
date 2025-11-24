package com.moa.backend.domain.reward.dto;

import com.moa.backend.domain.reward.entity.RewardDisclosureCategory;
import lombok.*;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 한글 설명: 프론트에서 리워드 생성/수정 시 서버로 보내는
 * "전자상거래 정보고시" 입력 값 DTO.
 *
 * - 이 DTO는 RewardRequest 안에서 사용된다.
 * - RewardFactory에서 이 값을 Reward 엔티티의 disclosure* 컬럼에 JSON으로 직렬화하여 저장한다.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RewardDisclosureRequestDTO {

    // ================= 공통 정보고시 필드 =================

    /**
     * 한글 설명: 정보고시 카테고리 (의류, 식품, 화장품 등)
     * - Enum name() 이 DB에는 문자열로 저장된다.
     */
    private RewardDisclosureCategory category;

    /** 한글 설명: 제조자 (수입품인 경우 수입자 포함) */
    private String manufacturer;

    /** 한글 설명: 브랜드 / 제품명 */
    private String brandName;

    /** 한글 설명: 원산지 (제조국) */
    private String originCountry;

    /** 한글 설명: 모델명 또는 품목명 */
    private String modelName;

    /** 한글 설명: 주요 소재 / 재질 */
    private String material;

    /** 한글 설명: 크기 / 치수 */
    private String size;

    /** 한글 설명: 제조연월 또는 유통기한/소비기한 등 */
    private String manufacturingOrExpiration;

    /** 한글 설명: 품질보증기준 */
    private String qualityAssurance;

    /** 한글 설명: A/S 책임자 및 전화번호(또는 채널) */
    private String asContact;

    /** 한글 설명: 배송 방법, 배송 기간 등 */
    private String deliveryInfo;

    /** 한글 설명: 배송비 및 추가 비용(도서산간, 설치비 등) */
    private String shippingFeeInfo;

    /** 한글 설명: KC 인증 여부 및 번호 등(해당하는 경우) */
    private String kcCertificationInfo;

    /** 한글 설명: 주의사항, 안전상 주의 문구 등 */
    private String precautions;

    /** 한글 설명: 기타 안내 사항 */
    private String etc;

    // ============== 카테고리별 상세 정보 (자유형식 JSON) ==============

    /**
     * 한글 설명: 카테고리별 세부 항목을 JSON 문자열로 담는 필드.
     * - 예: 의류 → {"fiberContent": "...", "washingMethod": "..."}
     * -     식품 → {"nutritionInfo": "...", "allergyInfo": "..."}
     * - 서버는 이 문자열을 그대로 Reward.disclosureCategorySpecificJson 에 저장한다.
     */
    private String categorySpecificJson;

    /**
     * 한글 설명: 공통 필드들을 Map 형태로 변환하여
     * JSON 직렬화에 사용하기 위한 유틸 메서드.
     * - RewardFactory에서 ObjectMapper.writeValueAsString()의 입력으로 사용된다.
     */
    public Map<String, Object> toCommonMap() {
        Map<String, Object> map = new LinkedHashMap<>();

        // null 이 아닌 값만 넣어 JSON을 깔끔하게 유지한다.
        if (manufacturer != null) map.put("manufacturer", manufacturer);
        if (brandName != null) map.put("brandName", brandName);
        if (originCountry != null) map.put("originCountry", originCountry);
        if (modelName != null) map.put("modelName", modelName);
        if (material != null) map.put("material", material);
        if (size != null) map.put("size", size);
        if (manufacturingOrExpiration != null) map.put("manufacturingOrExpiration", manufacturingOrExpiration);
        if (qualityAssurance != null) map.put("qualityAssurance", qualityAssurance);
        if (asContact != null) map.put("asContact", asContact);
        if (deliveryInfo != null) map.put("deliveryInfo", deliveryInfo);
        if (shippingFeeInfo != null) map.put("shippingFeeInfo", shippingFeeInfo);
        if (kcCertificationInfo != null) map.put("kcCertificationInfo", kcCertificationInfo);
        if (precautions != null) map.put("precautions", precautions);
        if (etc != null) map.put("etc", etc);

        return map;
    }
}
