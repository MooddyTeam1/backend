package com.moa.backend.domain.onboarding.dto;

import com.moa.backend.domain.onboarding.model.*;

/**
 * 한글 설명: 온보딩 2단계 (유입 경로, 예산, 경험, 알림 설정) 요청 DTO.
 */
public record SupporterOnboardingStep2Request(
        AcquisitionChannel acquisitionChannel,
        String acquisitionChannelEtc,
        BudgetRange budgetRange,
        FundingExperience fundingExperience,
        NotificationPreference notificationPreference
) {}
