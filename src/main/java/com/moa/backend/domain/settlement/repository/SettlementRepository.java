package com.moa.backend.domain.settlement.repository;

import com.moa.backend.domain.settlement.entity.Settlement;
import com.moa.backend.domain.settlement.entity.SettlementPayoutStatus;
import com.moa.backend.domain.settlement.entity.SettlementStatus;
import java.time.LocalDate;
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

    List<Settlement> findByProjectIdIn(List<Long> projectIds);

    /**
     * 메이커 ID로 정산 목록 조회 (페이지네이션)
     */
    org.springframework.data.domain.Page<Settlement> findByMaker_Id(Long makerId, org.springframework.data.domain.Pageable pageable);

    /**
     * 상태·기간별 netAmount 합계 (maker/project 필터)
     */
    @Query("""
        SELECT COALESCE(SUM(s.netAmount), 0)
        FROM Settlement s
        WHERE s.status = :status
          AND s.createdAt BETWEEN :startDateTime AND :endDateTime
          AND (:makerId IS NULL OR s.maker.id = :makerId)
          AND (:projectId IS NULL OR s.project.id = :projectId)
        """)
    Optional<Long> sumNetAmountByStatusAndCreatedAtBetweenAndFilters(
            @Param("status") SettlementStatus status,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime,
            @Param("makerId") Long makerId,
            @Param("projectId") Long projectId
    );

    /**
     * 기간 내 총 netAmount 합계 (필터)
     */
    @Query("""
        SELECT COALESCE(SUM(s.netAmount), 0)
        FROM Settlement s
        WHERE s.createdAt BETWEEN :startDateTime AND :endDateTime
          AND (:makerId IS NULL OR s.maker.id = :makerId)
          AND (:projectId IS NULL OR s.project.id = :projectId)
        """)
    Optional<Long> sumNetAmountByCreatedAtBetweenAndFilters(
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime,
            @Param("makerId") Long makerId,
            @Param("projectId") Long projectId
    );
}

