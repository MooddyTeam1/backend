package com.moa.backend.domain.reward.repository;

import com.moa.backend.domain.reward.entity.Reward;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RewardRepository extends JpaRepository<Reward, Long> {
}

