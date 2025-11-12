package com.moa.backend.domain.follow.repository;

import com.moa.backend.domain.follow.entity.SupporterFollowSupporter;
import com.moa.backend.domain.user.entity.SupporterProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SupporterFollowSupporterRepository extends JpaRepository<SupporterFollowSupporter, Long> {

    boolean existsByFollowerAndFollowing(SupporterProfile follower, SupporterProfile following);

    Optional<SupporterFollowSupporter> findByFollowerAndFollowing(SupporterProfile follower, SupporterProfile following);

    long countByFollower(SupporterProfile follower);

    long countByFollowing(SupporterProfile following);

    // 내가 팔로우한 서포터들 전체
    List<SupporterFollowSupporter> findByFollower(SupporterProfile follower);
}
