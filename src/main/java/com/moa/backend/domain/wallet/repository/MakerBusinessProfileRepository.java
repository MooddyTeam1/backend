package com.moa.backend.domain.wallet.repository;

import com.moa.backend.domain.wallet.entity.MakerBusinessProfile;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MakerBusinessProfileRepository extends JpaRepository<MakerBusinessProfile, Long> {

    Optional<MakerBusinessProfile> findByUser_Id(Long userId);
}
