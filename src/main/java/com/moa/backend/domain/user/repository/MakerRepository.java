package com.moa.backend.domain.user.repository;


import java.util.Optional;

import com.moa.backend.domain.user.entity.Maker;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MakerRepository extends JpaRepository<Maker, Long> {

    boolean existsByOwner_Id(Long ownerUserId);

    Optional<Maker> findByOwner_Id(Long ownerUserId);
}