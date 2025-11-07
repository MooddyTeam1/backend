package com.moa.backend.domain.payment.repository;

import com.moa.backend.domain.order.entity.Order;
import com.moa.backend.domain.payment.entity.Payment;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByPaymentKey(String paymentKey);
    
    boolean existsByOrder(Order order);
}

