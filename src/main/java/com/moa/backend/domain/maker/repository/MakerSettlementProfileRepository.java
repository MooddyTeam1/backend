package com.moa.backend.domain.maker.repository;

import com.moa.backend.domain.maker.entity.MakerSettlementProfile;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MakerSettlementProfileRepository extends JpaRepository<MakerSettlementProfile, Long> {

    Optional<MakerSettlementProfile> findByMaker_Id(Long makerId);
}
