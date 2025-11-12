package com.moa.backend.domain.wallet.repository;

import com.moa.backend.domain.wallet.entity.ProjectWallet;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * ProjectWallet 행을 조회/잠그는 레포지토리.
 * 프로젝트별 에스크로 장부는 동시에 수정될 수 있으므로 비관적 락 메서드를 제공한다.
 */
public interface ProjectWalletRepository extends JpaRepository<ProjectWallet, Long> {

    /**
     * 프로젝트별 지갑을 읽기 전용으로 조회.
     * 결제 금액을 보여주거나 상태만 확인할 때 사용한다.
     */
    Optional<ProjectWallet> findByProjectId(Long projectId);

    /**
     * 프로젝트 지갑을 수정하기 전 호출.
     * `SELECT ... FOR UPDATE`로 행을 잠가 동시 입금/환불 시 잔액 꼬임을 막는다.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select pw from ProjectWallet pw where pw.project.id = :projectId")
    Optional<ProjectWallet> findByProjectIdForUpdate(@Param("projectId") Long projectId);
}
