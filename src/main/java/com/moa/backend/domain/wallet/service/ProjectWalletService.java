package com.moa.backend.domain.wallet.service;

import com.moa.backend.domain.notification.entity.NotificationTargetType;
import com.moa.backend.domain.notification.entity.NotificationType;
import com.moa.backend.domain.notification.service.NotificationService;
import com.moa.backend.domain.order.entity.Order;
import com.moa.backend.domain.project.entity.Project;
import com.moa.backend.domain.settlement.entity.Settlement;
import com.moa.backend.domain.wallet.entity.ProjectWallet;
import com.moa.backend.domain.wallet.entity.ProjectWalletTransaction;
import com.moa.backend.domain.wallet.entity.ProjectWalletTransactionType;
import com.moa.backend.domain.wallet.repository.ProjectWalletRepository;
import com.moa.backend.domain.wallet.repository.ProjectWalletTransactionRepository;
import com.moa.backend.global.error.AppException;
import com.moa.backend.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 프로젝트별 에스크로 지갑을 다루는 서비스.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectWalletService {

    private final ProjectWalletRepository projectWalletRepository;
    private final ProjectWalletTransactionRepository transactionRepository;
    private final NotificationService notificationService;

    /**
     * 프로젝트 생성 시 지갑을 함께 만든다.
     */
    @Transactional
    public ProjectWallet createForProject(Project project) {
        return projectWalletRepository.findByProjectId(project.getId())
                .orElseGet(() -> {
                    ProjectWallet wallet = ProjectWallet.of(project);
                    projectWalletRepository.save(wallet);
                    log.info("ProjectWallet 생성: projectId={}", project.getId());
                    return wallet;
                });
    }

    /**
     * 결제 승인으로 들어온 금액을 에스크로 잔액에 적립한다.
     */
    @Transactional
    public void deposit(Long projectId, long amount, Order order) {
        ProjectWallet wallet = projectWalletRepository.findByProjectIdForUpdate(projectId)
                .orElseThrow(() -> new AppException(ErrorCode.PROJECT_WALLET_NOT_FOUND));

        wallet.deposit(amount);
        projectWalletRepository.save(wallet);

        ProjectWalletTransaction tx = ProjectWalletTransaction.of(
                wallet,
                ProjectWalletTransactionType.DEPOSIT,
                amount,
                wallet.getEscrowBalance(),
                order,
                null,
                "결제 승인: orderId=" + order.getId()
        );
        transactionRepository.save(tx);

        log.info("ProjectWallet 입금: projectId={}, amount={}, balance={}",
                projectId, amount, wallet.getEscrowBalance());
    }

    /**
     * 환불 시 에스크로 잔액을 줄이고 거래 로그를 남긴다.
     */
    @Transactional
    public void refund(Long projectId, long amount, Order order) {
        ProjectWallet wallet = projectWalletRepository.findByProjectIdForUpdate(projectId)
                .orElseThrow(() -> new AppException(ErrorCode.PROJECT_WALLET_NOT_FOUND));

        wallet.refund(amount);
        projectWalletRepository.save(wallet);

        ProjectWalletTransaction tx = ProjectWalletTransaction.of(
                wallet,
                ProjectWalletTransactionType.REFUND,
                -amount,
                wallet.getEscrowBalance(),
                order,
                null,
                "환불: orderId=" + order.getId()
        );
        transactionRepository.save(tx);

        log.info("ProjectWallet 환불: projectId={}, amount={}, balance={}",
                projectId, amount, wallet.getEscrowBalance());
    }

    /**
     * 정산 생성 시 release 대기 금액을 기록한다.
     */
    @Transactional
    public void holdForSettlement(Project project, Settlement settlement, long amount) {
        ProjectWallet wallet = projectWalletRepository.findByProjectIdForUpdate(project.getId())
                .orElseThrow(() -> new AppException(ErrorCode.PROJECT_WALLET_NOT_FOUND));

        wallet.holdForRelease(amount);
        projectWalletRepository.save(wallet);

        ProjectWalletTransaction tx = ProjectWalletTransaction.of(
                wallet,
                ProjectWalletTransactionType.RELEASE_PENDING,
                amount,
                wallet.getEscrowBalance(),
                null,
                settlement,
                "정산 대기: settlementId=" + settlement.getId()
        );
        transactionRepository.save(tx);

        log.info("ProjectWallet hold: projectId={}, amount={}, pending={}",
                project.getId(), amount, wallet.getPendingRelease());

        Long receiverId = project.getMaker().getOwner().getId();

        notificationService.send(
                receiverId,
                "정산 예정 안내",
                "[" + project.getTitle() + "] 프로젝트의 정산(" + amount + "원)이 예정되었습니다.",
                NotificationType.MAKER,
                NotificationTargetType.PROJECT,
                project.getId()
        );
    }

    /**
     * 선지급/잔금 지급 시 실제 release를 수행한다.
     */
    @Transactional
    public void releaseToMaker(Project project, Settlement settlement, long amount) {
        ProjectWallet wallet = projectWalletRepository.findByProjectIdForUpdate(project.getId())
                .orElseThrow(() -> new AppException(ErrorCode.PROJECT_WALLET_NOT_FOUND));

        wallet.release(amount);
        projectWalletRepository.save(wallet);

        ProjectWalletTransaction tx = ProjectWalletTransaction.of(
                wallet,
                ProjectWalletTransactionType.RELEASE,
                -amount,
                wallet.getEscrowBalance(),
                null,
                settlement,
                "메이커 송금: settlementId=" + settlement.getId()
        );
        transactionRepository.save(tx);

        log.info("ProjectWallet release: projectId={}, amount={}, balance={}",
                project.getId(), amount, wallet.getEscrowBalance());

        Long receiverId = project.getMaker().getOwner().getId();

        notificationService.send(
                receiverId,
                "정산 지급 완료",
                "[" + project.getTitle() + "] 정산 금액 " + amount + "원이 지급되었습니다.",
                NotificationType.MAKER,
                NotificationTargetType.PROJECT,
                project.getId()
        );
    }
}
