package com.moa.backend.domain.onboarding.dto;

import com.moa.backend.domain.onboarding.model.ProjectStylePreference;

import java.util.List;

/**
 * 한글 설명: 온보딩 1단계 (관심 카테고리 + 선호 스타일) 요청 DTO.
 */
public record SupporterOnboardingStep1Request(
        // 예: ["TECH","DESIGN","FOOD"]
        List<String> interestCategories,

        // 예: ["PRACTICAL","UNIQUE_GOODS"]
        List<ProjectStylePreference> preferredStyles
) {}
