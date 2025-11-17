package com.moa.backend.domain.settlement.repository;

import com.moa.backend.domain.settlement.entity.Settlement;
import com.moa.backend.domain.settlement.entity.SettlementPayoutStatus;
import com.moa.backend.domain.settlement.entity.SettlementStatus;
import jakarta.persistence.LockModeType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    /**
     * 선지급 재시도 대상 목록 (생성된 지 10분 이상, retryCount 미만) 조회.
     */
    List<Settlement> findTop50ByFirstPaymentStatusAndCreatedAtBeforeAndRetryCountLessThan(
            SettlementPayoutStatus firstPaymentStatus,
            LocalDateTime createdAt,
            int retryCount
    );

    /**
     * 특정 Settlement 상태 목록 조회 (예: FIRST_PAID).
     */
    List<Settlement> findAllByStatus(SettlementStatus status);
}

