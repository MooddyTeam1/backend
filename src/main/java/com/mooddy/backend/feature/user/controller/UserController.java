package com.mooddy.backend.feature.user.controller;

import com.mooddy.backend.feature.user.domain.User;
import com.mooddy.backend.feature.user.dto.OnboardingRequest;
import com.mooddy.backend.feature.user.dto.UserSearchResponseDto;
import com.mooddy.backend.feature.user.service.UserOnboardingService;
import com.mooddy.backend.feature.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final UserOnboardingService userOnboardingService;

    @PatchMapping("/onboarding")
    public ResponseEntity<Void> completedOnboarding(
            @AuthenticationPrincipal User user,
            @RequestBody OnboardingRequest request
    ) {
        userOnboardingService.completedOnboarding(user.getId(), request);

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

