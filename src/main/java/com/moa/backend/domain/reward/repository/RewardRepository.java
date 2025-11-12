package com.moa.backend.domain.reward.repository;

import com.moa.backend.domain.reward.entity.Reward;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 리워드 조회/검색 전용 리포지토리.
 */
public interface RewardRepository extends JpaRepository<Reward, Long> {

    /** 프로젝트 내 특정 리워드 ID 목록 조회 */
    List<Reward> findByProjectIdAndIdIn(Long projectId, Collection<Long> rewardIds);

    /** 프로젝트와 리워드 ID를 함께 검증 */
    Optional<Reward> findByIdAndProjectId(Long rewardId, Long projectId);
}

