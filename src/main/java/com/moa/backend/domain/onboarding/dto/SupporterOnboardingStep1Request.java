package com.moa.backend.domain.onboarding.dto;

import com.moa.backend.domain.onboarding.model.ProjectStylePreference;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * 한글 설명: 온보딩 1단계 (관심 카테고리 + 선호 스타일) 요청 DTO.
 */
@Schema(description = "온보딩 1단계 요청: 관심 카테고리/스타일")
public record SupporterOnboardingStep1Request(
        // 예: ["TECH","DESIGN","FOOD"]
        @Schema(description = "관심 카테고리 목록", example = "[\"TECH\",\"DESIGN\",\"FOOD\"]")
        List<String> interestCategories,

        // 예: ["PRACTICAL","UNIQUE_GOODS"]
        @Schema(description = "선호 프로젝트 스타일", example = "[\"PRACTICAL\",\"UNIQUE_GOODS\"]")
        List<ProjectStylePreference> preferredStyles
) {}
