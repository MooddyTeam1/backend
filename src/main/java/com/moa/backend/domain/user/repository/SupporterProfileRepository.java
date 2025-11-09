package com.moa.backend.domain.user.repository;


import java.util.Optional;

import com.moa.backend.domain.user.entity.SupporterProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupporterProfileRepository extends JpaRepository<SupporterProfile, Long> {

    boolean existsByUserId(Long userId);

    Optional<SupporterProfile> findByUserId(Long userId);
}