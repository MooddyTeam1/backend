package com.moa.backend.domain.wallet.repository;

import com.moa.backend.domain.wallet.entity.MakerWallet;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MakerWalletRepository extends JpaRepository<MakerWallet, Long> {

    Optional<MakerWallet> findByMaker_Id(Long makerId);

    /**
     * 메이커 지갑 잔액을 갱신하기 직전에 행 단위 비관적 락을 건다.
     * JPA가 내부적으로 `select ... from maker_wallets where maker_id = ? for update` 형태의 쿼리를 실행,
     * 따라서 현재 트랜잭션이 끝날 때까지 다른 세션은 같은 행을 수정하거나 읽지 못한다.
     * 덕분에 동시 결제·정산 시 잔액이 서로 덮어쓰이는 상황을 예방할 수 있다.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select mw from MakerWallet mw where mw.maker.id = :makerId")
    Optional<MakerWallet> findByMakerIdForUpdate(@Param("makerId") Long makerId);
}
