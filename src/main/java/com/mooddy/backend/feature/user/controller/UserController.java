package com.mooddy.backend.feature.user.controller;

import com.mooddy.backend.feature.user.domain.User;
import com.mooddy.backend.feature.user.dto.UserSearchResponseDto;
import com.mooddy.backend.feature.user.repository.UserRepository;
import com.mooddy.backend.feature.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;
    private final UserService userService;

    @PatchMapping("/{id}/onboarding") // 상태만 전달
    public ResponseEntity<Void> completedOnboarding(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setOnboardingCompleted(true);

        userRepository.save(user);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserSearchResponseDto>> searchUsers(
            @RequestParam String nickname,
            @AuthenticationPrincipal User currentUser
    ) {
        // 현재 로그인한 사용자의 ID를 가져와서 검색 결과에서 제외
        List<UserSearchResponseDto> searchResults =
                userService.searchUsersByNickname(nickname, currentUser.getId());

        return ResponseEntity.ok(searchResults);
    }
}

