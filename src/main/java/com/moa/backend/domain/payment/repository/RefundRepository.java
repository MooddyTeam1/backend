package com.moa.backend.domain.payment.repository;

import com.moa.backend.domain.payment.entity.Refund;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefundRepository extends JpaRepository<Refund, Long> {
}

