package com.moa.backend.domain.wallet.service;

import com.moa.backend.domain.maker.entity.Maker;
import com.moa.backend.domain.settlement.entity.Settlement;
import com.moa.backend.domain.wallet.entity.MakerWallet;
import com.moa.backend.domain.wallet.entity.WalletTransaction;
import com.moa.backend.domain.wallet.entity.WalletTransactionType;
import com.moa.backend.domain.wallet.repository.MakerWalletRepository;
import com.moa.backend.domain.wallet.repository.WalletTransactionRepository;
import com.moa.backend.global.error.AppException;
import com.moa.backend.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 메이커 정산 지갑을 관리하는 서비스.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MakerWalletService {

    private final MakerWalletRepository makerWalletRepository;
    private final WalletTransactionRepository transactionRepository;

    /**
     * 메이커 생성 시 지갑도 함께 만든다.
     */
    @Transactional
    /**
     * 메이커당 지갑은 하나만 존재해야 하므로, 없을 때만 생성하고 이미 있으면 그대로 반환한다.
     */
    public MakerWallet createForMaker(Maker maker) {
        return makerWalletRepository.findByMakerId(maker.getId())
                .orElseGet(() -> {
                    MakerWallet wallet = MakerWallet.of(maker);
                    makerWalletRepository.save(wallet);
                    log.info("MakerWallet 생성: makerId={}", maker.getId());
                    return wallet;
                });
    }

    /**
     * 잔금 대기 금액 적립 (잔금 준비용).
     */
    @Transactional
    public void addPending(Maker maker, long amount) {
        if (amount <= 0) {
            return;
        }
        MakerWallet wallet = makerWalletRepository.findByMakerIdForUpdate(maker.getId())
                .orElseThrow(() -> new AppException(ErrorCode.MAKER_WALLET_NOT_FOUND));

        wallet.addPending(amount);
        makerWalletRepository.save(wallet);

        log.info("MakerWallet pending 적립: makerId={}, amount={}, pending={}",
                maker.getId(), amount, wallet.getPendingBalance());
    }

    /**
     * 선지급 금액을 가용 잔액에 적립하고 거래 로그를 남긴다.
     */
    @Transactional
    public void creditAvailable(Maker maker, long amount, Settlement settlement) {
        MakerWallet wallet = makerWalletRepository.findByMakerIdForUpdate(maker.getId())
                .orElseThrow(() -> new AppException(ErrorCode.MAKER_WALLET_NOT_FOUND));

        wallet.creditAvailable(amount);
        makerWalletRepository.save(wallet);

        WalletTransaction tx = WalletTransaction.of(
                wallet,
                WalletTransactionType.SETTLEMENT_FIRST,
                amount,
                wallet.getAvailableBalance(),
                settlement,
                "선지급: settlementId=" + settlement.getId()
        );
        transactionRepository.save(tx);

        log.info("MakerWallet 선지급: makerId={}, amount={}, available={}, pending={}",
                maker.getId(), amount, wallet.getAvailableBalance(), wallet.getPendingBalance());
    }

    /**
     * 잔금 지급 시 pending → available로 이동시키고 로그를 남긴다.
     */
    @Transactional
    public void releasePendingToAvailable(Maker maker, long amount, Settlement settlement) {
        MakerWallet wallet = makerWalletRepository.findByMakerIdForUpdate(maker.getId())
                .orElseThrow(() -> new AppException(ErrorCode.MAKER_WALLET_NOT_FOUND));

        wallet.releasePendingToAvailable(amount);
        makerWalletRepository.save(wallet);

        WalletTransaction tx = WalletTransaction.of(
                wallet,
                WalletTransactionType.SETTLEMENT_FINAL,
                amount,
                wallet.getAvailableBalance(),
                settlement,
                "잔금 확정: settlementId=" + settlement.getId()
        );
        transactionRepository.save(tx);

        log.info("MakerWallet 잔금 확정: makerId={}, amount={}, pending={}, available={}",
                maker.getId(), amount, wallet.getPendingBalance(), wallet.getAvailableBalance());
    }

    /**
     * 환불 시 이미 지급된 금액을 회수한다.
     */
    @Transactional
    public void refundDebit(Maker maker, long amount) {
        MakerWallet wallet = makerWalletRepository.findByMakerIdForUpdate(maker.getId())
                .orElseThrow(() -> new AppException(ErrorCode.MAKER_WALLET_NOT_FOUND));

        wallet.refundDebit(amount);
        makerWalletRepository.save(wallet);

        WalletTransaction tx = WalletTransaction.of(
                wallet,
                WalletTransactionType.REFUND_DEBIT,
                -amount,
                wallet.getAvailableBalance(),
                null,
                "환불 회수"
        );
        transactionRepository.save(tx);

        log.info("MakerWallet 환불 회수: makerId={}, amount={}, available={}",
                maker.getId(), amount, wallet.getAvailableBalance());
    }
}
