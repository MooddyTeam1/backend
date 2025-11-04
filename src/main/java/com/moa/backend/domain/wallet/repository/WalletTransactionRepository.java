package com.moa.backend.domain.wallet.repository;

import com.moa.backend.domain.wallet.entity.WalletTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, Long> {
}

