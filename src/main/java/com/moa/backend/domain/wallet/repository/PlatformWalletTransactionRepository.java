package com.moa.backend.domain.wallet.repository;

import com.moa.backend.domain.wallet.entity.PlatformWalletTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 플랫폼 지갑 거래 로그를 저장/조회하는 레포지토리.
 * 로그 테이블은 새 행만 추가하는 append-only 구조이므로 별도 락 없이 기본 CRUD만 제공한다.
 */
public interface PlatformWalletTransactionRepository
        extends JpaRepository<PlatformWalletTransaction, Long> {
}
