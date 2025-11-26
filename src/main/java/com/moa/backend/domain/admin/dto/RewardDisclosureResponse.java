package com.moa.backend.domain.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 한글 설명: 리워드 정보고시 응답 DTO.
 * - category: 의류, 식품, 디지털 콘텐츠 등 카테고리
 * - common: 공통 정보고시
 * - categorySpecific: 카테고리별 세부 항목 (맵 구조)
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RewardDisclosureResponse {

    private String category;
    private RewardCommonDisclosureResponse common;
    private Map<String, Object> categorySpecific;

    /**
     * 한글 설명: disclosureJson(String) 을 파싱하여 생성하는 정적 메서드는
     * ObjectMapper 주입 후 추후 구현 추천.
     */
}

