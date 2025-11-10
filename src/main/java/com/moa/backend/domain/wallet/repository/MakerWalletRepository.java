package com.moa.backend.domain.wallet.repository;

import com.moa.backend.domain.wallet.entity.MakerWallet;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MakerWalletRepository extends JpaRepository<MakerWallet, Long> {

    Optional<MakerWallet> findByMaker_Id(Long makerId);
}
