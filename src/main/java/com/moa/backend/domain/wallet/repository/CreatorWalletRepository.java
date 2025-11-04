package com.moa.backend.domain.wallet.repository;

import com.moa.backend.domain.wallet.entity.CreatorWallet;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CreatorWalletRepository extends JpaRepository<CreatorWallet, Long> {

    Optional<CreatorWallet> findByUser_Id(Long userId);
}
