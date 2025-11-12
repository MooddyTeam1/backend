package com.moa.backend.domain.wallet.service;

import com.moa.backend.domain.payment.entity.Payment;
import com.moa.backend.domain.settlement.entity.Settlement;
import com.moa.backend.domain.wallet.entity.PlatformWallet;
import com.moa.backend.domain.wallet.entity.PlatformWalletTransaction;
import com.moa.backend.domain.wallet.entity.PlatformWalletTransactionType;
import com.moa.backend.domain.wallet.repository.PlatformWalletRepository;
import com.moa.backend.domain.wallet.repository.PlatformWalletTransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 플랫폼 실계좌 장부를 관리하는 서비스.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PlatformWalletService {

    private final PlatformWalletRepository platformWalletRepository;
    private final PlatformWalletTransactionRepository transactionRepository;

    /**
     * 플랫폼 지갑을 가져오거나 초기화한다.
     */
    @Transactional
    public PlatformWallet getOrCreate() {
        return platformWalletRepository.findTopByOrderByIdAsc()
                .orElseGet(() -> platformWalletRepository.save(PlatformWallet.initialize()));
    }

    /**
     * 결제 승인 후 PG에서 순입금된 금액을 기록한다.
     */
    @Transactional
    public void deposit(long netAmount, Payment payment) {
        PlatformWallet wallet = getOrCreateWithLock();

        wallet.deposit(netAmount);
        platformWalletRepository.save(wallet);

        PlatformWalletTransaction tx = PlatformWalletTransaction.of(
                wallet,
                PlatformWalletTransactionType.PAYMENT_DEPOSIT,
                netAmount,
                wallet.getTotalBalance(),
                payment.getOrder().getProject(),
                null,
                "PG 입금: paymentId=" + payment.getId()
        );
        transactionRepository.save(tx);

        log.info("PlatformWallet 입금: amount={}, balance={}",
                netAmount, wallet.getTotalBalance());
    }

    /**
     * 메이커 정산 송금 시 플랫폼 지갑에서 금액을 차감한다.
     */
    @Transactional
    public void recordMakerWithdrawal(Settlement settlement, long amount) {
        PlatformWallet wallet = getOrCreateWithLock();

        wallet.withdraw(amount);
        platformWalletRepository.save(wallet);

        PlatformWalletTransaction tx = PlatformWalletTransaction.of(
                wallet,
                PlatformWalletTransactionType.WITHDRAW_TO_MAKER,
                -amount,
                wallet.getTotalBalance(),
                settlement.getProject(),
                settlement,
                "메이커 송금: settlementId=" + settlement.getId()
        );
        transactionRepository.save(tx);

        log.info("PlatformWallet 메이커 송금: amount={}, balance={}",
                amount, wallet.getTotalBalance());
    }

    /**
     * 고객 환불 송금 내역을 기록한다.
     */
    @Transactional
    public void recordRefund(Payment payment, long amount) {
        PlatformWallet wallet = getOrCreateWithLock();

        wallet.refundOut(amount);
        platformWalletRepository.save(wallet);

        PlatformWalletTransaction tx = PlatformWalletTransaction.of(
                wallet,
                PlatformWalletTransactionType.REFUND_OUT,
                -amount,
                wallet.getTotalBalance(),
                payment.getOrder().getProject(),
                null,
                "환불 송금: paymentId=" + payment.getId()
        );
        transactionRepository.save(tx);

        log.info("PlatformWallet 환불: amount={}, balance={}",
                amount, wallet.getTotalBalance());
    }

    private PlatformWallet getOrCreateWithLock() {
        return platformWalletRepository.findFirstByOrderByIdAsc()
                .orElseGet(() -> platformWalletRepository.save(PlatformWallet.initialize()));
    }
}
