package com.moa.backend.domain.wallet.repository;

import com.moa.backend.domain.wallet.entity.PlatformWalletTransaction;
import com.moa.backend.domain.wallet.entity.PlatformWalletTransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 플랫폼 지갑 거래 로그를 저장/조회하는 레포지토리.
 * 로그 테이블은 새 행만 추가하는 append-only 구조이므로 별도 락 없이 기본 CRUD만 제공한다.
 */
public interface PlatformWalletTransactionRepository
        extends JpaRepository<PlatformWalletTransaction, Long> {

    /**
     * 타입/기간별 금액 합계 (출금 타입은 음수로 합산됨)
     */
    @Query("""
        SELECT COALESCE(SUM(t.amount), 0)
        FROM PlatformWalletTransaction t
        WHERE t.type = :type
        AND t.createdAt BETWEEN :startDateTime AND :endDateTime
        """)
    Optional<Long> sumAmountByTypeAndCreatedAtBetween(
            @Param("type") PlatformWalletTransactionType type,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime
    );
}
