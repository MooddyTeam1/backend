package com.moa.backend.domain.reward.dto;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moa.backend.domain.reward.entity.Reward;
import com.moa.backend.domain.reward.entity.RewardDisclosureCategory;
import lombok.*;

import java.util.Collections;
import java.util.Map;

/**
 * 한글 설명: 리워드 조회 시 클라이언트로 내려줄
 * "전자상거래 정보고시" 응답 DTO.
 *
 * - Reward 엔티티의 disclosure* 컬럼에서 값을 읽어와서 생성한다.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RewardDisclosureResponseDTO {

    // 한글 설명: 정보고시 카테고리
    private RewardDisclosureCategory category;

    /**
     * 한글 설명: 공통 정보고시 항목들을 key-value 형태로 내려준다.
     * - Reward.disclosureCommonJson(JSON)을 역직렬화한 결과.
     * - 프론트에서는 이 Map을 그대로 출력하거나, 폼에 다시 바인딩해서 수정할 수 있다.
     */
    private Map<String, Object> common;

    /**
     * 한글 설명: 카테고리별 상세 정보(JSON)를 역직렬화한 결과.
     * - 구조는 카테고리별로 자유롭게 정의한다.
     */
    private Map<String, Object> categorySpecific;

    /**
     * 한글 설명: Reward 엔티티에서 정보고시 데이터를 읽어와
     * RewardDisclosureResponseDTO 로 변환하는 팩토리 메서드.
     *
     * - 엔티티에 정보고시 데이터가 없으면 빈 객체를 반환한다 (null이 아닌 빈 객체로 응답에 포함되도록).
     */
    public static RewardDisclosureResponseDTO from(Reward reward) {
        if (reward == null) {
            // 한글 설명: null 대신 빈 객체를 반환하여 JSON 응답에 disclosure 필드가 항상 포함되도록 함
            return RewardDisclosureResponseDTO.builder()
                    .category(null)
                    .common(Collections.emptyMap())
                    .categorySpecific(Collections.emptyMap())
                    .build();
        }

        ObjectMapper mapper = new ObjectMapper();

        Map<String, Object> commonMap = Collections.emptyMap();
        Map<String, Object> specificMap = Collections.emptyMap();

        // 한글 설명: disclosureCommonJson 또는 disclosureCategorySpecificJson이 있으면 disclosure 정보가 있는 것으로 간주
        boolean hasDisclosureData = reward.getDisclosureCommonJson() != null 
                || reward.getDisclosureCategorySpecificJson() != null 
                || reward.getDisclosureCategory() != null;

        if (!hasDisclosureData) {
            // 한글 설명: disclosure 정보가 전혀 없는 경우 빈 객체 반환
            return RewardDisclosureResponseDTO.builder()
                    .category(null)
                    .common(Collections.emptyMap())
                    .categorySpecific(Collections.emptyMap())
                    .build();
        }

        try {
            if (reward.getDisclosureCommonJson() != null) {
                commonMap = mapper.readValue(
                        reward.getDisclosureCommonJson(),
                        new TypeReference<Map<String, Object>>() {}
                );
            }
        } catch (Exception e) {
            // 한글 설명: 파싱 실패 시에도 API 전체가 죽지 않도록 빈 맵으로 대체
            commonMap = Collections.emptyMap();
        }

        try {
            if (reward.getDisclosureCategorySpecificJson() != null) {
                specificMap = mapper.readValue(
                        reward.getDisclosureCategorySpecificJson(),
                        new TypeReference<Map<String, Object>>() {}
                );
            }
        } catch (Exception e) {
            specificMap = Collections.emptyMap();
        }

        // 한글 설명: category가 null이 아닌 경우에만 Enum으로 변환
        RewardDisclosureCategory categoryEnum = null;
        if (reward.getDisclosureCategory() != null) {
            try {
                categoryEnum = RewardDisclosureCategory.valueOf(reward.getDisclosureCategory());
            } catch (Exception e) {
                // 한글 설명: Enum 변환 실패 시 null로 유지
                categoryEnum = null;
            }
        }

        return RewardDisclosureResponseDTO.builder()
                .category(categoryEnum)
                .common(commonMap)
                .categorySpecific(specificMap)
                .build();
    }
}
