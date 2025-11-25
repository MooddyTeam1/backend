package com.moa.backend.domain.onboarding.dto;

import com.moa.backend.domain.onboarding.model.*;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 한글 설명: 온보딩 2단계 (유입 경로, 예산, 경험, 알림 설정) 요청 DTO.
 */
@Schema(description = "온보딩 2단계 요청: 유입 경로/예산/경험/알림")
public record SupporterOnboardingStep2Request(
        @Schema(description = "유입 경로", example = "SOCIAL")
        AcquisitionChannel acquisitionChannel,
        @Schema(description = "유입 경로 기타 입력", example = "지인 추천")
        String acquisitionChannelEtc,
        @Schema(description = "월 예산 범위", example = "UNDER_50K")
        BudgetRange budgetRange,
        @Schema(description = "펀딩 경험", example = "FIRST_TIME")
        FundingExperience fundingExperience,
        @Schema(description = "알림 선호도", example = "IMPORTANT_ONLY")
        NotificationPreference notificationPreference
) {}
