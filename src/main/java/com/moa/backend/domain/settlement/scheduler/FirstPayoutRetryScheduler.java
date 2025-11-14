package com.moa.backend.domain.settlement.scheduler;

import com.moa.backend.domain.settlement.entity.Settlement;
import com.moa.backend.domain.settlement.entity.SettlementPayoutStatus;
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
 * Step 2: 선지급 재시도 스케줄러.
 * - 매 10분마다 PENDING 상태의 정산을 다시 시도한다.
 * - 3회 이상 실패하면 FAILED로 마킹해 운영 알림 대상으로 남겨둔다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FirstPayoutRetryScheduler {

    private static final int MAX_RETRY = 3;

    private final SettlementRepository settlementRepository;
    private final SettlementService settlementService;
    private final SchedulerSupport schedulerSupport;

    /**
     * 10분 이상 대기 중인 선지급 건을 찾아 재시도한다.
     */
    @Scheduled(cron = "0 */10 * * * *")
    @Transactional
    public void retryFirstPayout() {
        schedulerSupport.runSafely("first-payout-retry", () -> {
            LocalDateTime threshold = LocalDateTime.now().minusMinutes(10);
            List<Settlement> targets = settlementRepository
                    .findTop50ByFirstPaymentStatusAndCreatedAtBeforeAndRetryCountLessThan(
                            SettlementPayoutStatus.PENDING,
                            threshold,
                            MAX_RETRY
                    );

            log.info("[first-payout-retry] 대상={}건", targets.size());
            targets.forEach(this::processSettlementSafely);
            return null;
        });
    }

    private void processSettlementSafely(Settlement settlement) {
        try {
            settlementService.payFirstPayout(settlement.getId());
            settlement.resetRetryCount();
            log.info("[first-payout-retry] 성공 settlementId={}", settlement.getId());
        } catch (Exception ex) {
            settlement.incrementRetryCount();
            if (settlement.getRetryCount() >= MAX_RETRY) {
                settlement.markFirstPaymentFailed();
                log.error("[first-payout-retry] 최종 실패 settlementId={}",
                        settlement.getId(), ex);
                // TODO: Slack/알림 연동
            } else {
                log.warn("[first-payout-retry] 실패 settlementId={} retryCount={}",
                        settlement.getId(), settlement.getRetryCount(), ex);
            }
        }
    }
}
