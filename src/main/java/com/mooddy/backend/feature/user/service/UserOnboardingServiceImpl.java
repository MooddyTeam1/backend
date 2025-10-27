package com.mooddy.backend.feature.user.service;

import com.mooddy.backend.feature.user.domain.User;
import com.mooddy.backend.feature.user.dto.OnboardingRequest;
import com.mooddy.backend.feature.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;

@Service
@RequiredArgsConstructor
public class UserOnboardingServiceImpl implements UserOnboardingService{

    private final UserRepository userRepository;

    @Override
    public void completedOnboarding(Long userId, OnboardingRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수없습니다"));

        user.setFavoriteGenres(new HashSet<>(request.getGenre()));
        user.setFavoriteArtists(new HashSet<>(request.getArtist()));
        user.setOnboardingCompleted(true);

        userRepository.save(user);
    }
}
