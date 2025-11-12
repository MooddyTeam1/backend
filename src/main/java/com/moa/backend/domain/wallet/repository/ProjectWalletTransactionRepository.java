package com.moa.backend.domain.wallet.repository;

import com.moa.backend.domain.wallet.entity.ProjectWalletTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 프로젝트 지갑 거래 로그 레포지토리.
 * 신규 로그만 계속 추가하는 append-only 구조이므로 기본 CRUD면 충분하다.
 */
public interface ProjectWalletTransactionRepository
        extends JpaRepository<ProjectWalletTransaction, Long> {
}
