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

    /**
     * 한글 설명: 메이커 ID로 메이커를 조회하며 owner를 함께 로드 (fetch join).
     * - 관리자 메이커 프로필 조회에서 사용.
     * - owner 정보가 필요한 경우 이 메서드를 사용.
     * - INNER JOIN FETCH를 사용하여 owner가 반드시 존재하는 경우만 조회.
     */
    @Query("""
            SELECT DISTINCT m
            FROM Maker m
            INNER JOIN FETCH m.owner
            WHERE m.id = :makerId
            """)
    Optional<Maker> findByIdWithOwner(@Param("makerId") Long makerId);
}
