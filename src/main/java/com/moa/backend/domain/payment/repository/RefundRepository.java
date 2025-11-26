package com.moa.backend.domain.payment.repository;

import com.moa.backend.domain.payment.entity.Refund;
import com.moa.backend.domain.payment.entity.RefundStatus;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RefundRepository extends JpaRepository<Refund, Long> {

    Long countByStatusAndCreatedAtBetween(
            RefundStatus status,
            LocalDateTime startDateTime,
            LocalDateTime endDateTime
    );

    @Query("""
        SELECT COALESCE(SUM(r.amount), 0)
        FROM Refund r
        WHERE r.status = :status
          AND r.createdAt BETWEEN :startDateTime AND :endDateTime
        """)
    Optional<Long> sumAmountByStatusAndCreatedAtBetween(
            @Param("status") RefundStatus status,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime
    );
}
