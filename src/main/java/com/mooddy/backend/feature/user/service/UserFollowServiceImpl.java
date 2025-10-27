package com.mooddy.backend.feature.user.service;

import com.mooddy.backend.feature.user.domain.Follow;
import com.mooddy.backend.feature.user.domain.User;
import com.mooddy.backend.feature.user.dto.UserFollowResponse;
import com.mooddy.backend.feature.user.repository.FollowRepository;
import com.mooddy.backend.feature.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserFollowServiceImpl implements UserFollowService{

    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void toggleFollow(Long userId, Long targetId) {
        boolean exists = followRepository.existsByFollowerIdAndFollowingId(userId, targetId);

        if (exists) {
            followRepository.deleteByFollowerIdAndFollowingId(userId, targetId);
        } else {
            // 팔로우 하는 사람
            User follower = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));
            // 팔로우 당하는 사람
            User following = userRepository.findById(targetId)
                    .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

            Follow follow = new Follow();
            follow.setFollower(follower);
            follow.setFollowing(following);

            followRepository.save(follow);
        }
    }

    // 내가 팔로우 하는 사람들 목록
    @Override
    public List<UserFollowResponse> getFollowing(Long userId) {
        return followRepository.findByFollowerId(userId).stream()
                .map(f -> UserFollowResponse.builder()
                        .id(f.getFollowing().getId())
                        .nickname(f.getFollowing().getNickname())
                        .profileImageUrl(f.getFollowing().getProfileImageUrl()) // 이미지 포함
                        .build())
                .toList();
    }

    // 나를 팔로우 하는 사람들 목록
    @Override
    public List<UserFollowResponse> getFollowers(Long userId) {
        return followRepository.findByFollowingId(userId).stream()
                .map(f -> UserFollowResponse.builder()
                        .id(f.getFollower().getId())
                        .nickname(f.getFollower().getNickname())
                        .profileImageUrl(f.getFollower().getProfileImageUrl())
                        .build())
                .toList();
    }

    @Override
    public boolean isFollowing(Long userId, Long targetId) {
        return followRepository.existsByFollowerIdAndFollowingId(userId, targetId);
    }
}
