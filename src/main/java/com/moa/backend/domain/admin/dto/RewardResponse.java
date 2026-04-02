package com.moa.backend.domain.admin.dto;

import com.moa.backend.domain.reward.dto.RewardDisclosureResponseDTO;
import com.moa.backend.domain.reward.dto.select.OptionGroupResponse;
import com.moa.backend.domain.reward.entity.Reward;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 한글 설명: 관리자 심사 상세 화면에서 사용하는 리워드 DTO.
 * - 정보고시(disclosure), 옵션 구성(optionConfig)을 포함.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "관리자 심사용 리워드 응답")
public class RewardResponse {

    // 기본 리워드 정보
    @Schema(description = "리워드 ID", example = "5001")
    private Long id;
    @Schema(description = "리워드 제목", example = "얼리버드 텀블러 1개 세트")
    private String title; // 한글 설명: 실제 엔티티는 name 필드 사용
    @Schema(description = "리워드 설명", example = "친환경 소재 텀블러 1개와 세척솔 구성")
    private String description;
    @Schema(description = "리워드 가격(원)", example = "29000")
    private Long price;
    @Schema(description = "수량 제한", example = "300")
    private Integer limitQty; // 한글 설명: 실제 엔티티는 stockQuantity 필드 사용
    @Schema(description = "예상 배송 월", example = "2026-01-01")
    private LocalDate estShippingMonth; // 한글 설명: 실제 엔티티는 estimatedDeliveryDate 필드 사용
    @Schema(description = "판매 가능 여부", example = "true")
    @Builder.Default
    private Boolean available = Boolean.FALSE; // 한글 설명: 실제 엔티티는 active 필드 사용

    // 한글 설명: 옵션 구성 (색상/사이즈 등)
    @Schema(description = "옵션 구성 정보")
    private RewardOptionConfigResponse optionConfig;

    // 한글 설명: 전자상거래법 정보고시
    @Schema(description = "전자상거래법 정보고시")
    private RewardDisclosureResponse disclosure;

    /**
     * 한글 설명: Reward 엔티티에서 RewardResponse 로 변환하는 정적 메서드.
     * - 정보고시(disclosure) 정보를 파싱하여 포함.
     * - 옵션 구성(optionConfig)을 OptionGroupResponse에서 변환하여 포함.
     */
    public static RewardResponse from(Reward reward) {
        // 한글 설명: 정보고시 변환
        RewardDisclosureResponse disclosureResponse = convertDisclosure(reward);

        // 한글 설명: 옵션 구성 변환
        RewardOptionConfigResponse optionConfigResponse = convertOptionConfig(reward);

        return RewardResponse.builder()
                .id(reward.getId())
                .title(reward.getName()) // name -> title로 매핑
                .description(reward.getDescription())
                .price(reward.getPrice())
                .limitQty(reward.getStockQuantity()) // stockQuantity -> limitQty로 매핑
                .estShippingMonth(reward.getEstimatedDeliveryDate()) // estimatedDeliveryDate -> estShippingMonth로 매핑
                .available(reward.isActive()) // active -> available로 매핑
                .optionConfig(optionConfigResponse)
                .disclosure(disclosureResponse)
                .build();
    }

    /**
     * 한글 설명: Reward 엔티티의 옵션 그룹을 RewardOptionConfigResponse로 변환.
     * - 기존 OptionGroupResponse를 활용하여 변환.
     */
    private static RewardOptionConfigResponse convertOptionConfig(Reward reward) {
        if (reward.getOptionGroups() == null || reward.getOptionGroups().isEmpty()) {
            return RewardOptionConfigResponse.builder()
                    .hasOptions(false)
                    .options(Collections.emptyList())
                    .build();
        }

        // 한글 설명: OptionGroupResponse를 RewardOptionItemResponse로 변환
        List<RewardOptionItemResponse> optionItems = reward.getOptionGroups().stream()
                .map(optionGroup -> {
                    // 한글 설명: OptionGroupResponse로 먼저 변환
                    OptionGroupResponse groupResponse = OptionGroupResponse.from(optionGroup);

                    // 한글 설명: 옵션 값 목록에서 선택지 추출
                    List<String> choices = groupResponse.getOptionValues() != null
                            ? groupResponse.getOptionValues().stream()
                                    .map(optionValue -> optionValue.getOptionValue())
                                    .collect(Collectors.toList())
                            : Collections.emptyList();

                    return RewardOptionItemResponse.builder()
                            .name(groupResponse.getGroupName()) // 옵션 그룹명 (예: "색상")
                            .type("select") // 한글 설명: 현재는 select 타입만 지원
                            .required(true) // 한글 설명: 옵션이 있으면 필수로 간주
                            .choices(choices) // 한글 설명: 옵션 값 목록 (예: ["블랙", "화이트"])
                            .build();
                })
                .collect(Collectors.toList());

        return RewardOptionConfigResponse.builder()
                .hasOptions(true)
                .options(optionItems)
                .build();
    }

    /**
     * 한글 설명: Reward 엔티티의 정보고시 데이터를 RewardDisclosureResponse로 변환.
     * - RewardDisclosureResponseDTO를 중간 단계로 사용하여 변환.
     */
    private static RewardDisclosureResponse convertDisclosure(Reward reward) {
        // 한글 설명: 기존 RewardDisclosureResponseDTO.from()을 사용하여 정보고시 파싱
        RewardDisclosureResponseDTO dto = RewardDisclosureResponseDTO.from(reward);

        if (dto == null) {
            return null;
        }

        // 한글 설명: RewardDisclosureResponseDTO의 common Map을
        // RewardCommonDisclosureResponse로 변환
        RewardCommonDisclosureResponse commonResponse = convertCommonDisclosure(dto.getCommon());

        return RewardDisclosureResponse.builder()
                .category(dto.getCategory() != null ? dto.getCategory().name() : null)
                .common(commonResponse)
                .categorySpecific(dto.getCategorySpecific())
                .build();
    }

    /**
     * 한글 설명: 공통 정보고시 Map을 RewardCommonDisclosureResponse 객체로 변환.
     * - 새로운 구조: RewardCommonDisclosureRequestDTO가 직접 JSON으로 직렬화되어 저장됨.
     * - 필드 이름이 DTO 필드 이름과 정확히 일치합니다.
     */
    private static RewardCommonDisclosureResponse convertCommonDisclosure(Map<String, Object> commonMap) {
        if (commonMap == null || commonMap.isEmpty()) {
            return null;
        }

        // 한글 설명: Map에서 값을 추출하여 객체 생성
        // 새로운 구조에서는 필드 이름이 DTO 필드 이름과 정확히 일치
        return RewardCommonDisclosureResponse.builder()
                .manufacturer(getStringValue(commonMap, "manufacturer"))
                .importer(getStringValue(commonMap, "importer"))
                .countryOfOrigin(getStringValue(commonMap, "countryOfOrigin"))
                .manufacturingDate(getStringValue(commonMap, "manufacturingDate"))
                .releaseDate(getStringValue(commonMap, "releaseDate"))
                .expirationDate(getStringValue(commonMap, "expirationDate"))
                .qualityAssurance(getStringValue(commonMap, "qualityAssurance"))
                .asContactName(getStringValue(commonMap, "asContactName"))
                .asContactPhone(getStringValue(commonMap, "asContactPhone"))
                .shippingFee(getLongValue(commonMap, "shippingFee"))
                .installationFee(getLongValue(commonMap, "installationFee"))
                .kcCertification(getBooleanValue(commonMap, "kcCertification"))
                .kcCertificationNumber(getStringValue(commonMap, "kcCertificationNumber"))
                .functionalCertification(getBooleanValue(commonMap, "functionalCertification"))
                .importDeclaration(getBooleanValue(commonMap, "importDeclaration"))
                .build();
    }

    /**
     * 한글 설명: Map에서 String 값을 안전하게 추출.
     */
    private static String getStringValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) {
            return null;
        }
        return value.toString();
    }

    /**
     * 한글 설명: Map에서 Long 값을 안전하게 추출.
     */
    private static Long getLongValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        try {
            return Long.parseLong(value.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * 한글 설명: Map에서 Boolean 값을 안전하게 추출.
     */
    private static Boolean getBooleanValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) {
            return null;
        }
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        if (value instanceof String) {
            return Boolean.parseBoolean((String) value);
        }
        return null;
    }
}
