package com.moa.backend.domain.settlement.scheduler;

import com.moa.backend.domain.order.entity.Order;
import com.moa.backend.domain.order.entity.OrderStatus;
import com.moa.backend.domain.order.repository.OrderRepository;
import com.moa.backend.domain.payment.service.PaymentService;
import com.moa.backend.domain.project.entity.Project;
import com.moa.backend.domain.project.entity.ProjectLifecycleStatus;
import com.moa.backend.domain.project.entity.ProjectResultStatus;
import com.moa.backend.domain.project.entity.ProjectReviewStatus;
import com.moa.backend.domain.project.repository.ProjectRepository;
import com.moa.backend.domain.settlement.service.SettlementService;
import com.moa.backend.global.scheduler.SchedulerSupport;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Step 1: 펀딩 종료 + 정산 생성 자동화 스케줄러.
 * - 매일 02시에 종료일이 지난 프로젝트를 조회
 * - 목표 달성 여부에 따라 Settlement 생성 혹은 자동 환불 실행
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FundingCloseScheduler {

    private final ProjectRepository projectRepository;
    private final OrderRepository orderRepository;
    private final SettlementService settlementService;
    private final PaymentService paymentService;
    private final SchedulerSupport schedulerSupport;

    /**
     * 종료일이 지난 LIVE 프로젝트를 처리한다.
     */
    @Scheduled(cron = "0 0 2 * * *")
    @Transactional
    public void closeFundingProjects() {
        schedulerSupport.runSafely("funding-close", () -> {
            LocalDate inclusiveEndDate = LocalDate.now().plusDays(1); // <= today 를 before 비교로 표현
            List<Project> targets = projectRepository
                    .findByLifecycleStatusAndReviewStatusAndResultStatusAndEndDateBefore(
                            ProjectLifecycleStatus.LIVE,
                            ProjectReviewStatus.APPROVED,
                            ProjectResultStatus.NONE,
                            inclusiveEndDate
                    );

            log.info("[funding-close] 대상 프로젝트={}건", targets.size());
            targets.forEach(this::processProjectSafely);
            return null;
        });
    }

    /**
     * 단일 프로젝트 처리 시 예외가 전체 배치를 멈추지 않도록 보호한다.
     */
    private void processProjectSafely(Project project) {
        try {
            processProject(project);
        } catch (Exception ex) {
            log.error("[funding-close] 프로젝트 처리 실패 projectId={}", project.getId(), ex);
        }
    }

    /**
     * 주문 총액을 집계해 목표 달성 여부에 따라 성공/실패 분기 처리.
     */
    private void processProject(Project project) {
        long paidAmount = orderRepository
                .sumTotalAmountByProjectIdAndStatus(project.getId(), OrderStatus.PAID)
                .orElse(0L);

        if (paidAmount >= defaultLong(project.getGoalAmount())) {
            handleSuccess(project, paidAmount);
        } else {
            handleFailure(project, paidAmount);
        }
    }

    /**
     * 목표 달성 프로젝트는 상태를 SUCCESS로 바꾸고 Settlement를 생성한다.
     */
    private void handleSuccess(Project project, long paidAmount) {
        project.setLifecycleStatus(ProjectLifecycleStatus.ENDED);
        project.setResultStatus(ProjectResultStatus.SUCCESS);
        projectRepository.save(project);

        settlementService.createSettlement(project.getId());
        log.info("[funding-close] SUCCESS projectId={} paid={} goal={}",
                project.getId(), paidAmount, project.getGoalAmount());
    }

    /**
     * 목표 미달 프로젝트는 FAILED로 상태를 변경하고 모든 결제를 환불한다.
     */
    private void handleFailure(Project project, long paidAmount) {
        project.setLifecycleStatus(ProjectLifecycleStatus.ENDED);
        project.setResultStatus(ProjectResultStatus.FAILED);
        projectRepository.save(project);

        List<Order> paidOrders = orderRepository.findAllByProjectIdAndStatus(
                project.getId(), OrderStatus.PAID);

        paidOrders.forEach(order -> {
            try {
                paymentService.cancelByOrder(order, "펀딩 실패 자동 환불");
            } catch (Exception ex) {
                log.error("[funding-close] 환불 실패 orderId={} projectId={}",
                        order.getId(), project.getId(), ex);
            }
        });
        log.info("[funding-close] FAILED projectId={} paid={} goal={} refunded={}orders",
                project.getId(), paidAmount, project.getGoalAmount(), paidOrders.size());
    }

    /**
     * goalAmount가 null인 예외 케이스를 방지하기 위한 헬퍼.
     */
    private long defaultLong(Long value) {
        return value == null ? 0L : value;
    }
}
