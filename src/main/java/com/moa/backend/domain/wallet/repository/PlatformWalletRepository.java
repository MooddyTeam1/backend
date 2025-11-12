package com.moa.backend.domain.wallet.repository;

import com.moa.backend.domain.wallet.entity.PlatformWallet;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

/**
 * 플랫폼 전체 자금 장부(싱글턴)를 다루는 레포지토리.
 * PG 입금/메이커 송금/환불이 모두 한 장부에서 일어나므로,
 * 실계좌 잔액과 DB 숫자가 어긋나지 않도록 update 전 비관적 락을 사용한다.
 */
public interface PlatformWalletRepository extends JpaRepository<PlatformWallet, Long> {

    /**
     * 플랫폼 지갑 목록 중 가장 오래된 행을 잠근다.
     * DB마다 다른 id 정책을 쓰더라도 첫 번째 행만 사용하도록 통일한다.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<PlatformWallet> findFirstByOrderByIdAsc();

    /**
     * 잠금 없이 최초 행을 조회한다.
     */
    Optional<PlatformWallet> findTopByOrderByIdAsc();
}
