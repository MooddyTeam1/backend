package com.moa.backend.settlement;

import com.moa.backend.domain.maker.entity.Maker;
import com.moa.backend.domain.maker.repository.MakerRepository;
import com.moa.backend.domain.order.entity.Order;
import com.moa.backend.domain.order.repository.OrderRepository;
import com.moa.backend.domain.project.entity.Project;
import com.moa.backend.domain.project.repository.ProjectRepository;
import com.moa.backend.domain.settlement.entity.Settlement;
import com.moa.backend.domain.settlement.entity.SettlementStatus;
import com.moa.backend.domain.settlement.repository.SettlementRepository;
import com.moa.backend.domain.settlement.service.SettlementService;
import com.moa.backend.domain.user.entity.User;
import com.moa.backend.domain.user.repository.UserRepository;
import com.moa.backend.domain.wallet.entity.MakerWallet;
import com.moa.backend.domain.wallet.entity.PlatformWallet;
import com.moa.backend.domain.wallet.entity.ProjectWallet;
import com.moa.backend.domain.wallet.repository.MakerWalletRepository;
import com.moa.backend.domain.wallet.repository.PlatformWalletRepository;
import com.moa.backend.domain.wallet.repository.ProjectWalletRepository;
import com.moa.backend.domain.wallet.service.MakerWalletService;
import com.moa.backend.domain.wallet.service.ProjectWalletService;
import com.moa.backend.global.util.MoneyCalculator;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class SettlementServiceTest {

    @Autowired
    private SettlementService settlementService;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private SettlementRepository settlementRepository;
    @Autowired
    private ProjectWalletRepository projectWalletRepository;
    @Autowired
    private ProjectWalletService projectWalletService;
    @Autowired
    private MakerWalletService makerWalletService;
    @Autowired
    private MakerWalletRepository makerWalletRepository;
    @Autowired
    private PlatformWalletRepository platformWalletRepository;
    @Autowired
    private MakerRepository makerRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("정산 생성 → 선지급 → FINAL_READY → 잔금 지급 흐름")
    void settlementFlow() {
        // given: 에스크로/메이커/플랫폼 지갑을 모두 초기화한 정산 대상 주문
        ProjectOrderContext context = createProjectWithPaidOrder(100_000L);
        Project project = context.project();
        projectWalletService.createForProject(project);
        projectWalletService.deposit(project.getId(), context.order().getTotalAmount(), context.order());
        makerWalletService.createForMaker(project.getMaker());
        seedPlatformWallet(calculateAfterPgAmount(context.order().getTotalAmount()));

        // when: 정산 생성 → 선지급 → FINAL_READY → 잔금까지 순차 실행
        Settlement settlement = settlementService.createSettlement(project.getId());
        ProjectWallet projectWallet = projectWalletRepository.findByProjectId(project.getId())
                .orElseThrow();
        assertThat(projectWallet.getPendingRelease()).isEqualTo(settlement.getNetAmount());

        settlementService.payFirstPayout(settlement.getId());
        settlement = settlementRepository.findById(settlement.getId()).orElseThrow();
        MakerWallet makerWallet = makerWalletRepository.findByMakerId(project.getMaker().getId())
                .orElseThrow();
        assertThat(makerWallet.getAvailableBalance()).isEqualTo(settlement.getFirstPaymentAmount());
        assertThat(makerWallet.getPendingBalance()).isEqualTo(settlement.getFinalPaymentAmount());
        assertThat(settlement.getStatus()).isEqualTo(SettlementStatus.FIRST_PAID);

        settlement.markFinalReady();
        settlementRepository.save(settlement);

        settlementService.payFinalPayout(settlement.getId());

        // then: 메이커는 Net 금액 전액, 에스크로에는 (총액-순입금)=수수료 합계만 잔존, 상태는 COMPLETED
        settlement = settlementRepository.findById(settlement.getId()).orElseThrow();
        makerWallet = makerWalletRepository.findByMakerId(project.getMaker().getId()).orElseThrow();
        projectWallet = projectWalletRepository.findByProjectId(project.getId()).orElseThrow();
        long expectedFeeHold = context.order().getTotalAmount() - settlement.getNetAmount();

        assertThat(makerWallet.getAvailableBalance()).isEqualTo(settlement.getNetAmount());
        assertThat(makerWallet.getPendingBalance()).isZero();
        assertThat(projectWallet.getPendingRelease()).isZero();
        assertThat(projectWallet.getEscrowBalance()).isEqualTo(expectedFeeHold);
        assertThat(settlement.getStatus()).isEqualTo(SettlementStatus.COMPLETED);
    }

    /**
     * 테스트용 프로젝트/주문/메이커를 생성하고 주문을 PAID 상태로 만든다.
     */
    private ProjectOrderContext createProjectWithPaidOrder(long amount) {
        User user = userRepository.save(User.createUser(
                "tester-" + UUID.randomUUID() + "@moa.com",
                "encodedPw",
                "테스터"
        ));
        Maker maker = makerRepository.save(Maker.create(user, "테스트메이커"));
        Project project = projectRepository.save(Project.builder()
                .maker(maker)
                .title("정산 테스트 " + UUID.randomUUID())
                .goalAmount(10_000L)
                .build());

        Order order = orderRepository.save(Order.create(
                user,
                project,
                "ORD-" + UUID.randomUUID(),
                "정산테스트",
                amount,
                "수령인",
                "010-0000-0000",
                "서울시",
                "101동",
                "12345"
        ));
        order.markPaid();
        orderRepository.save(order);
        return new ProjectOrderContext(project, order);
    }

    private void seedPlatformWallet(long amount) {
        PlatformWallet wallet = platformWalletRepository.findTopByOrderByIdAsc()
                .orElseGet(() -> platformWalletRepository.save(PlatformWallet.initialize()));
        wallet.deposit(amount);
        platformWalletRepository.save(wallet);
    }

    private long calculateAfterPgAmount(long totalPaid) {
        long pgFee = MoneyCalculator.percentageOf(totalPaid, 0.05);
        return MoneyCalculator.subtract(totalPaid, pgFee);
    }

    private record ProjectOrderContext(Project project, Order order) {
    }
}
