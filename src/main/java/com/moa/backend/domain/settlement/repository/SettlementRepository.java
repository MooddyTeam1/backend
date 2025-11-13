package com.moa.backend.domain.settlement.repository;

import com.moa.backend.domain.settlement.entity.Settlement;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SettlementRepository extends JpaRepository<Settlement, Long> {

    /**
     * 프로젝트별 정산 단건 조회
     */
    Optional<Settlement> findByProjectId(Long projectId);

    /**
     * 정산 처리 중 중복 실행을 막기 위한 비관적 락 조회
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Settlement s WHERE s.id = :id")
    Optional<Settlement> findByIdForUpdate(@Param("id") Long id);
}

