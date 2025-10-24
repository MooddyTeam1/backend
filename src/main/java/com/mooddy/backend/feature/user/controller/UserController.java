package com.mooddy.backend.feature.user.controller;

import com.mooddy.backend.feature.user.domain.User;
import com.mooddy.backend.feature.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;

    @PatchMapping("/{id}/onboarding") // 상태만 전달
    public ResponseEntity<Void> completedOnboarding(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setOnboardingCompleted(true);

        userRepository.save(user);

        return ResponseEntity.ok().build();
    }
}
