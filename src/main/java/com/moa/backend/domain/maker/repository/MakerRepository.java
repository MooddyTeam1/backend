package com.moa.backend.domain.maker.repository;

import com.moa.backend.domain.maker.entity.Maker;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface MakerRepository extends JpaRepository<Maker, Long> {

    boolean existsByOwner_Id(Long ownerUserId);

    Optional<Maker> findByOwner_Id(Long ownerUserId);

    Long countByCreatedAtBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);

    // 한글 설명: Maker.owner.id (owner_user_id) 로 메이커를 조회한다.
    @Query("select m from Maker m where m.owner.id = :userId")
    Optional<Maker> findByUserId(@Param("userId") Long userId);
}
