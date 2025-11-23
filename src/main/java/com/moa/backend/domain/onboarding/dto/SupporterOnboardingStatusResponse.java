package com.moa.backend.domain.onboarding.dto;

import com.moa.backend.domain.onboarding.model.OnboardingStatus;

/**
 * 한글 설명: 프론트에서 온보딩 UI 노출 여부를 판단하기 위한 상태 응답 DTO
 */
public record SupporterOnboardingStatusResponse(
        OnboardingStatus status,  // NOT_STARTED / SKIPPED / COMPLETED
        boolean step1Completed,
        boolean step2Completed,
        boolean allCompleted      // status == COMPLETED 인지 여부
) {
    public static SupporterOnboardingStatusResponse of(
            OnboardingStatus status,
            boolean step1Completed,
            boolean step2Completed
    ) {
        boolean all = (status == OnboardingStatus.COMPLETED);
        return new SupporterOnboardingStatusResponse(status, step1Completed, step2Completed, all);
    }
}