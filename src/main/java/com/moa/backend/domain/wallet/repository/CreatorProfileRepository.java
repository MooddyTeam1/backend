package com.moa.backend.domain.wallet.repository;

import com.moa.backend.domain.wallet.entity.CreatorProfile;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CreatorProfileRepository extends JpaRepository<CreatorProfile, Long> {

    Optional<CreatorProfile> findByUser_Id(Long userId);
}
