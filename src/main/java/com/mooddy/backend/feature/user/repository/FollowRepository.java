package com.mooddy.backend.feature.user.repository;

import com.mooddy.backend.feature.user.domain.Follow;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    // 내가 팔로우한 유저 목록
    List<Follow> findByFollowerId(Long followerId);

    // 나를 팔로우한 유저 목록
    List<Follow> findByFollowingId(Long followingId);

    boolean existsByFollowerIdAndFollowingId(Long followerId, Long followingId);

    void deleteByFollowerIdAndFollowingId(Long followerId, Long followingId);
}
