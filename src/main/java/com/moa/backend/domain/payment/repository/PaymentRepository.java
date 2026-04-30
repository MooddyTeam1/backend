package com.moa.backend.domain.payment.repository;

import com.moa.backend.domain.order.entity.Order;
import com.moa.backend.domain.payment.entity.Payment;
import com.moa.backend.domain.payment.entity.PaymentStatus;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM Payment p WHERE p.order.id IN :orderIds")
    void deleteByOrder_IdIn(@Param("orderIds") Collection<Long> orderIds);

    Optional<Payment> findByPaymentKey(String paymentKey);
    
    boolean existsByOrder(Order order);

    Optional<Payment> findByOrder(Order order);

    Long countByCreatedAtBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);

    Long countByStatusAndCreatedAtBetween(
            PaymentStatus status,
            LocalDateTime startDateTime,
            LocalDateTime endDateTime
    );
}
