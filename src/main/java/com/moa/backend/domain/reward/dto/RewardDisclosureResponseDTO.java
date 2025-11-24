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
     * - 엔티티에 정보고시 데이터가 없으면 null 을 반환한다.
     */
    public static RewardDisclosureResponseDTO from(Reward reward) {
        if (reward == null || reward.getDisclosureCategory() == null) {
            return null;
        }

        ObjectMapper mapper = new ObjectMapper();

        Map<String, Object> commonMap = Collections.emptyMap();
        Map<String, Object> specificMap = Collections.emptyMap();

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

        RewardDisclosureCategory categoryEnum =
                RewardDisclosureCategory.valueOf(reward.getDisclosureCategory());

        return RewardDisclosureResponseDTO.builder()
                .category(categoryEnum)
                .common(commonMap)
                .categorySpecific(specificMap)
                .build();
    }
}
