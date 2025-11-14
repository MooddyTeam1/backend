package com.moa.backend.domain.settlement.scheduler;

import com.moa.backend.domain.order.entity.DeliveryStatus;
import com.moa.backend.domain.order.entity.Order;
import com.moa.backend.domain.order.repository.OrderRepository;
import com.moa.backend.domain.settlement.entity.Settlement;
import com.moa.backend.domain.settlement.entity.SettlementStatus;
import com.moa.backend.domain.settlement.repository.SettlementRepository;
import com.moa.backend.domain.settlement.service.SettlementService;
import com.moa.backend.global.scheduler.SchedulerSupport;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Step 4: 배송 완료 14일 후 자동 구매확정 + 잔금 지급 스케줄러.
 * - 매일 04:00에 배송 완료 주문을 자동 확정하고
 * - 모든 주문이 확정된 프로젝트는 잔금을 지급한다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FinalPayoutScheduler {

    private static final int AUTO_CONFIRM_DAYS = 14;

    private final OrderRepository orderRepository;
    private final SettlementRepository settlementRepository;
    private final SettlementService settlementService;
    private final SchedulerSupport schedulerSupport;

    /**
     * 배송 완료 후 14일이 지난 주문을 자동 확정하고 잔금을 지급한다.
     */
    @Scheduled(cron = "0 0 4 * * *")
    @Transactional
    public void confirmAndPayFinal() {
        schedulerSupport.runSafely("final-payout", () -> {
            autoConfirmOrders();
            triggerFinalPayout();
            return null;
        });
    }

    /**
     * 배송 완료 후 14일이 지난 주문을 자동으로 구매확정 상태로 전환한다.
     */
    private void autoConfirmOrders() {
        LocalDateTime threshold = LocalDateTime.now().minusDays(AUTO_CONFIRM_DAYS);
        List<Order> targets = orderRepository
                .findAllByDeliveryStatusAndDeliveryCompletedAtBefore(
                        DeliveryStatus.DELIVERED,
                        threshold
                );

        log.info("[final-payout] 자동 확정 대상={}건", targets.size());
        targets.forEach(order -> {
            try {
                order.confirm();
                log.info("[final-payout] 주문 자동 확정 orderId={}", order.getId());
            } catch (Exception ex) {
                log.error("[final-payout] 주문 확정 실패 orderId={}", order.getId(), ex);
            }
        });
    }

    /**
     * 모든 주문이 확정된 프로젝트의 Settlement를 FINAL_READY → COMPLETED 로 전환한다.
     */
    private void triggerFinalPayout() {
        List<Settlement> candidates = settlementRepository.findAllByStatus(SettlementStatus.FIRST_PAID);
        log.info("[final-payout] 잔금 후보={}건", candidates.size());

        candidates.forEach(settlement -> {
            try {
                boolean hasPendingOrders = orderRepository.existsByProjectIdAndDeliveryStatusNot(
                        settlement.getProject().getId(),
                        DeliveryStatus.CONFIRMED
                );

                if (hasPendingOrders) {
                    log.debug("[final-payout] 아직 미확정 주문 존재 settlementId={}", settlement.getId());
                    return;
                }

                settlement.markFinalReady();
                settlementRepository.save(settlement);

                settlementService.payFinalPayout(settlement.getId());
                log.info("[final-payout] 잔금 완료 settlementId={}", settlement.getId());
            } catch (Exception ex) {
                log.error("[final-payout] 잔금 처리 실패 settlementId={}", settlement.getId(), ex);
            }
        });
    }
}
