package com.moa.backend.domain.follow.repository;

import com.moa.backend.domain.follow.entity.SupporterFollowMaker;
import com.moa.backend.domain.maker.entity.Maker;
import com.moa.backend.domain.user.entity.SupporterProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SupporterFollowMakerRepository extends JpaRepository<SupporterFollowMaker, Long> {

    boolean existsBySupporterAndMaker(SupporterProfile supporter, Maker maker);

    Optional<SupporterFollowMaker> findBySupporterAndMaker(SupporterProfile supporter, Maker maker);

    long countByMaker(Maker maker);

    // 내가 팔로우한 메이커들 전체
    List<SupporterFollowMaker> findBySupporter(SupporterProfile supporter);
}
