package com.moa.backend.domain.reward.repository;

import com.moa.backend.domain.reward.entity.Reward;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RewardRepository extends JpaRepository<Reward, Long> {

    List<Reward> findByProjectIdAndIdIn(Long projectId, Collection<Long> rewardIds);

    Optional<Reward> findByIdAndProjectId(Long rewardId, Long projectId);
}

