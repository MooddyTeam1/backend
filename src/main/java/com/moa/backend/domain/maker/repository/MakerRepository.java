package com.moa.backend.domain.maker.repository;

import com.moa.backend.domain.maker.entity.Maker;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;

public interface MakerRepository extends JpaRepository<Maker, Long> {

    boolean existsByOwner_Id(Long ownerUserId);

    Optional<Maker> findByOwner_Id(Long ownerUserId);

    Long countByCreatedAtBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);
}
