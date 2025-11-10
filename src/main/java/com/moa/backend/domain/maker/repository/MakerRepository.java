package com.moa.backend.domain.maker.repository;

import com.moa.backend.domain.maker.entity.Maker;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MakerRepository extends JpaRepository<Maker, Long> {

    Optional<Maker> findByOwner_Id(Long ownerId);
}
