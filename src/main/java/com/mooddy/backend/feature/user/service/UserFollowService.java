package com.mooddy.backend.feature.user.service;

import com.mooddy.backend.feature.user.dto.UserFollowResponse;

import java.util.List;

public interface UserFollowService {

    // 팔로우 토글
    void toggleFollow(Long userId, Long followingId);

    // 내가 팔로우하는 사람들
    List<UserFollowResponse> getFollowing(Long userId);

    // 나를 팔로우하는 사람들
    List<UserFollowResponse> getFollowers(Long userId);

    // 특정 유저 팔로우 중인지 조회
    boolean isFollowing(Long userId, Long followingId);
}
