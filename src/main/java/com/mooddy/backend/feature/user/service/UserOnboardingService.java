package com.mooddy.backend.feature.user.service;

import com.mooddy.backend.feature.user.dto.OnboardingRequest;

public interface UserOnboardingService {
    void completedOnboarding(Long userId, OnboardingRequest request);
}
