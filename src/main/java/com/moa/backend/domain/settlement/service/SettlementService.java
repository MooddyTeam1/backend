package com.moa.backend.domain.settlement.service;

import com.moa.backend.domain.order.entity.OrderStatus;
import com.moa.backend.domain.order.repository.OrderRepository;
import com.moa.backend.domain.project.entity.Project;
import com.moa.backend.domain.project.repository.ProjectRepository;
import com.moa.backend.domain.settlement.entity.Settlement;
import com.moa.backend.domain.settlement.repository.SettlementRepository;
import com.moa.backend.domain.wallet.service.ProjectWalletService;
import com.moa.backend.global.error.AppException;
import com.moa.backend.global.error.ErrorCode;
import com.moa.backend.global.util.MoneyCalculator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SettlementService {

    private final SettlementRepository settlementRepository;
    private final ProjectRepository projectRepository;
    private final OrderRepository orderRepository;
    private final ProjectWalletService projectWalletService;

    /**
     * 펀딩 성공 시 정산을 생성하고 ProjectWallet에 hold를 걸어둔다.
     */
    @Transactional
    public Settlement createSettlement(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new AppException(ErrorCode.PROJECT_NOT_FOUND));

        settlementRepository.findByProjectId(projectId).ifPresent(existing -> {
            throw new AppException(ErrorCode.SETTLEMENT_ALREADY_PROCESSED,
                    "이미 정산이 생성된 프로젝트입니다.");
        });

        long totalPaid = orderRepository.sumTotalAmountByProjectIdAndStatus(projectId, OrderStatus.PAID)
                .orElse(0L);

        if (totalPaid == 0) {
            throw new AppException(ErrorCode.SETTLEMENT_NOT_READY,
                    "결제 완료된 주문이 없습니다.");
        }

        long pgFee = MoneyCalculator.percentageOf(totalPaid, 0.05); // PG 수수료(5%)
        long afterPg = MoneyCalculator.subtract(totalPaid, pgFee);
        long platformFee = MoneyCalculator.percentageOf(afterPg, 0.10); // 플랫폼 수수료(10%)
        long netAmount = MoneyCalculator.subtract(afterPg, platformFee); // 메이커 순입금

        long firstPayment = MoneyCalculator.percentageOf(netAmount, 0.5); // 선지급 50%
        long finalPayment = netAmount - firstPayment;

        Settlement settlement = settlementRepository.save(
                Settlement.create(
                        project,
                        project.getMaker(),
                        totalPaid,
                        pgFee,
                        platformFee,
                        netAmount,
                        firstPayment,
                        finalPayment
                )
        );

        projectWalletService.holdForSettlement(project, settlement, netAmount);
        log.info("Settlement 생성: projectId={}, netAmount={}, first={}, final={}",
                projectId, netAmount, firstPayment, finalPayment);
        return settlement;
    }
}
