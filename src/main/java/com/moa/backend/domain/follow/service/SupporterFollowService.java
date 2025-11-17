package com.moa.backend.domain.follow.service;

import com.moa.backend.domain.follow.entity.SupporterFollowMaker;
import com.moa.backend.domain.follow.entity.SupporterFollowSupporter;
import com.moa.backend.domain.follow.repository.SupporterFollowMakerRepository;
import com.moa.backend.domain.follow.repository.SupporterFollowSupporterRepository;
import com.moa.backend.domain.maker.entity.Maker;
import com.moa.backend.domain.maker.repository.MakerRepository;
import com.moa.backend.domain.user.entity.SupporterProfile;
import com.moa.backend.domain.user.repository.SupporterProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

/**
 * 서포터 팔로우 관련 비즈니스 로직
 * - 서포터 ↔ 서포터
 * - 서포터 → 메이커
 */
@Service
@RequiredArgsConstructor
@Transactional
public class SupporterFollowService {

    private final SupporterFollowSupporterRepository supporterFollowSupporterRepository;
    private final SupporterFollowMakerRepository supporterFollowMakerRepository;
    private final SupporterProfileRepository supporterProfileRepository;
    private final MakerRepository makerRepository;

    /**
     * 현재 로그인한 서포터 프로필 조회
     * TODO: 실제 구현에서는 SecurityContext 에서 userId 꺼내서 supporterProfileRepository.findByUserId(...)로 조회
     */
    private SupporterProfile getCurrentSupporter() {
        throw new UnsupportedOperationException("SupporterFollowService.getCurrentSupporter()를 구현하세요.");
    }

    // ========== 서포터 ↔ 서포터 팔로우 ==========

    public void followSupporter(Long targetSupporterUserId) {
        SupporterProfile me = getCurrentSupporter();
        SupporterProfile target = supporterProfileRepository.findByUserId(targetSupporterUserId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 서포터입니다."));

        if (me.getUserId().equals(target.getUserId())) {
            throw new IllegalArgumentException("자기 자신은 팔로우할 수 없습니다.");
        }

        boolean already = supporterFollowSupporterRepository.existsByFollowerAndFollowing(me, target);
        if (already) {
            return; // 이미 팔로우 중이면 무시
        }

        supporterFollowSupporterRepository.save(SupporterFollowSupporter.of(me, target));
    }

    public void unfollowSupporter(Long targetSupporterUserId) {
        SupporterProfile me = getCurrentSupporter();
        SupporterProfile target = supporterProfileRepository.findByUserId(targetSupporterUserId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 서포터입니다."));

        supporterFollowSupporterRepository.findByFollowerAndFollowing(me, target)
                .ifPresent(supporterFollowSupporterRepository::delete);
    }

    // ========== 서포터 → 메이커 팔로우 ==========

    public void followMaker(Long makerId) {
        SupporterProfile me = getCurrentSupporter();
        Maker maker = makerRepository.findById(makerId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 메이커입니다."));

        boolean already = supporterFollowMakerRepository.existsBySupporterAndMaker(me, maker);
        if (already) {
            return;
        }

        supporterFollowMakerRepository.save(SupporterFollowMaker.of(me, maker));
    }

    public void unfollowMaker(Long makerId) {
        SupporterProfile me = getCurrentSupporter();
        Maker maker = makerRepository.findById(makerId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 메이커입니다."));

        supporterFollowMakerRepository.findBySupporterAndMaker(me, maker)
                .ifPresent(supporterFollowMakerRepository::delete);
    }

    // (선택) 팔로우 여부 조회 메서드

    @Transactional(readOnly = true)
    public boolean isFollowingSupporter(Long targetSupporterUserId) {
        SupporterProfile me = getCurrentSupporter();
        SupporterProfile target = supporterProfileRepository.findByUserId(targetSupporterUserId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 서포터입니다."));

        return supporterFollowSupporterRepository.existsByFollowerAndFollowing(me, target);
    }

    @Transactional(readOnly = true)
    public boolean isFollowingMaker(Long makerId) {
        SupporterProfile me = getCurrentSupporter();
        Maker maker = makerRepository.findById(makerId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 메이커입니다."));

        return supporterFollowMakerRepository.existsBySupporterAndMaker(me, maker);
    }
}
