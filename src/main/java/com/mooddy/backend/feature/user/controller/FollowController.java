package com.mooddy.backend.feature.user.controller;

import com.mooddy.backend.feature.user.domain.User;
import com.mooddy.backend.feature.user.dto.UserFollowResponse;
import com.mooddy.backend.feature.user.service.UserFollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/follow/users")
@RequiredArgsConstructor
public class FollowController {

    private final UserFollowService userFollowService;

    // 팔로우 토글
    @PostMapping("/{targetId}")
    public ResponseEntity<Void> toggleFollow(
            @AuthenticationPrincipal User user,
            @PathVariable Long targetId
    ) {
        userFollowService.toggleFollow(user.getId(), targetId);
        return ResponseEntity.ok().build();
    }

    // 내가 팔로우한 사람 목록 조회
    @GetMapping("/following")
    public ResponseEntity<List<UserFollowResponse>> getFollowing(
            @AuthenticationPrincipal  User user
    ) {
        List<UserFollowResponse> following = userFollowService.getFollowing(user.getId());
        return ResponseEntity.ok(following);
    }

    // 나를 팔로우한 사람 목록 조회
    @GetMapping("/followers")
    public ResponseEntity<List<UserFollowResponse>> getFollowers(
            @AuthenticationPrincipal  User user
    ) {
        List<UserFollowResponse> followers = userFollowService.getFollowers(user.getId());
        return ResponseEntity.ok(followers);
    }

    //  유저가 해당 유저 팔로우했는지 상태 조회
    @GetMapping("/following/{targetId}")
    public ResponseEntity<Boolean> isFollowing(
            @AuthenticationPrincipal  User user,
            @PathVariable Long targetId
    ) {
        boolean following = userFollowService.isFollowing(user.getId(), targetId);
        return ResponseEntity.ok(following);
    }
}