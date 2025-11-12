package com.moa.backend.domain.wallet.repository;

import com.moa.backend.domain.wallet.entity.PlatformWallet;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

/**
 * 플랫폼 전체 자금 장부(싱글턴)를 다루는 레포지토리.
 * PG 입금/메이커 송금/환불이 모두 한 장부에서 일어나므로,
 * 실계좌 잔액과 DB 숫자가 어긋나지 않도록 update 전 비관적 락을 사용한다.
 */
public interface PlatformWalletRepository extends JpaRepository<PlatformWallet, Long> {

    /**
     * 플랫폼 지갑은 항상 하나만 존재하며 초기화 시 id=1로 고정 저장한다.
     * 따라서 잔액을 조작하기 전에는 해당 행을 PESSIMISTIC_WRITE로 잠가
     * 동시 송금/환불 배치가 서로 잔액을 덮어쓰는 상황을 막는다.
     * (DB 수준에서 `SELECT ... FOR UPDATE`가 실행된다고 보면 된다)
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select pw from PlatformWallet pw where pw.id = 1L")
    Optional<PlatformWallet> findSingletonForUpdate();
}
