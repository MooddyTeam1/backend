package com.moa.backend.global.init;

import com.moa.backend.domain.order.entity.Order;
import com.moa.backend.domain.order.repository.OrderRepository;
import com.moa.backend.domain.payment.repository.PaymentRepository;
import com.moa.backend.domain.project.entity.Project;
import com.moa.backend.domain.project.repository.ProjectRepository;
import com.moa.backend.domain.reward.entity.Reward;
import com.moa.backend.domain.inventory.redis.StockSyncService;
import com.moa.backend.domain.reward.repository.RewardRepository;
import com.moa.backend.domain.wallet.repository.ProjectWalletTransactionRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * K6 부하 테스트로 쌓인 주문·재고를 dev 환경에서 되돌린다.
 * {@link UserSeedingService#LOAD_TEST_EMAIL_PREFIX} 유저의 주문만 삭제한다.
 */
@Slf4j
@Service
@Profile("dev")
@RequiredArgsConstructor
public class K6TestResetService {

    private final ProjectRepository projectRepository;
    private final RewardRepository rewardRepository;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final ProjectWalletTransactionRepository projectWalletTransactionRepository;
    private final StockSyncService stockSyncService;

    @Transactional
    public String resetK6LoadTestData() {
        return projectRepository
                .findByTitle(K6LoadTestConstants.PROJECT_TITLE)
                .map(this::resetForProject)
                .orElse("⚠️ K6 테스트 프로젝트가 없습니다. 먼저 POST /public/test-init 을 실행하세요.");
    }

    private String resetForProject(Project project) {
        Long projectId = project.getId();
        List<Order> orders =
                orderRepository.findAllByProject_IdAndUser_EmailStartingWith(
                        projectId, UserSeedingService.LOAD_TEST_EMAIL_PREFIX);
        int deleted = orders.size();
        if (!orders.isEmpty()) {
            List<Long> orderIds = orders.stream().map(Order::getId).toList();
            projectWalletTransactionRepository.deleteByOrder_IdIn(orderIds);
            paymentRepository.deleteByOrder_IdIn(orderIds);
            orderRepository.deleteAll(orders);
            log.info("K6 초기화: projectId={}, 삭제 주문 {}건", projectId, deleted);
        }

        List<Reward> rewards = rewardRepository.findByProject_Id(projectId);
        for (Reward r : rewards) {
            r.setStockQuantity(K6LoadTestConstants.REWARD_INITIAL_STOCK);
            r.setActive(true);
        }
        rewardRepository.saveAll(rewards);
        for (Reward r : rewards) {
            stockSyncService.syncSingleRewardEntity(r);
        }

        return String.format(
                "✅ K6 초기화 완료. projectId=%d | 삭제한 loadtest 주문: %d건 | 리워드 %d개 재고 → 각 %d",
                projectId,
                deleted,
                rewards.size(),
                K6LoadTestConstants.REWARD_INITIAL_STOCK);
    }
}
