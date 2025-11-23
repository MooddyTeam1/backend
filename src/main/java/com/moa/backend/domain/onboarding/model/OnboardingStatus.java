package com.moa.backend.domain.onboarding.model;

public enum OnboardingStatus {
    NOT_STARTED, // 가입 후 한 번도 온보딩 안 본 상태 (기본값)
    SKIPPED,     // 나중에 하기 눌러서 스킵
    COMPLETED    // 온보딩 플로우 끝까지 완료
}