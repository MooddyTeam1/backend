package com.moa.backend.wallet;

import com.moa.backend.domain.maker.entity.Maker;
import com.moa.backend.domain.maker.repository.MakerRepository;
import com.moa.backend.domain.order.entity.Order;
import com.moa.backend.domain.order.repository.OrderRepository;
import com.moa.backend.domain.project.entity.Project;
import com.moa.backend.domain.project.repository.ProjectRepository;
import com.moa.backend.domain.settlement.entity.Settlement;
import com.moa.backend.domain.settlement.repository.SettlementRepository;
import com.moa.backend.domain.user.entity.User;
import com.moa.backend.domain.user.repository.UserRepository;
import com.moa.backend.domain.wallet.entity.ProjectWallet;
import com.moa.backend.domain.wallet.entity.ProjectWalletTransaction;
import com.moa.backend.domain.wallet.entity.ProjectWalletTransactionType;
import com.moa.backend.domain.wallet.repository.ProjectWalletRepository;
import com.moa.backend.domain.wallet.repository.ProjectWalletTransactionRepository;
import com.moa.backend.domain.wallet.service.ProjectWalletService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class ProjectWalletServiceTest {

    @Autowired
    private ProjectWalletService projectWalletService;
    @Autowired
    private ProjectWalletRepository projectWalletRepository;
    @Autowired
    private ProjectWalletTransactionRepository projectWalletTransactionRepository;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private MakerRepository makerRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private SettlementRepository settlementRepository;

    @Test
    @DisplayName("결제 금액을 에스크로에 적립하면 잔액과 거래 로그가 기록된다")
    void deposit_정상동작() {
        // given: 결제 완료 주문과 빈 ProjectWallet
        Order order = prepareOrder(100_000L);
        projectWalletService.createForProject(order.getProject());

        // when: 결제 금액을 에스크로에 적립
        projectWalletService.deposit(order.getProject().getId(), 100_000L, order);

        // then: 잔액은 총액과 동일, 거래 로그는 DEPOSIT 1건
        ProjectWallet wallet = projectWalletRepository.findByProjectId(order.getProject().getId())
                .orElseThrow();
        assertThat(wallet.getEscrowBalance()).isEqualTo(100_000L);

        List<ProjectWalletTransaction> transactions = projectWalletTransactionRepository.findAll();
        assertThat(transactions).hasSize(1);
        ProjectWalletTransaction tx = transactions.get(0);
        assertThat(tx.getType()).isEqualTo(ProjectWalletTransactionType.DEPOSIT);
        assertThat(tx.getAmount()).isEqualTo(100_000L);
        assertThat(tx.getBalanceAfter()).isEqualTo(100_000L);
    }

    @Test
    @DisplayName("정산 hold 후 release 하면 pending→escrow 잔액이 기대대로 변한다")
    void hold와_release_흐름() {
        // given: 주문/정산 데이터를 준비하고 지갑 생성 및 입금
        Order order = prepareOrder(200_000L);
        projectWalletService.createForProject(order.getProject());
        projectWalletService.deposit(order.getProject().getId(), 200_000L, order);

        Settlement settlement = createSettlement(order.getProject(), order.getProject().getMaker(), 200_000L);

        // when: 정산 hold → release 순으로 진행
        projectWalletService.holdForSettlement(order.getProject(), settlement, 120_000L);
        ProjectWallet wallet = projectWalletRepository.findByProjectId(order.getProject().getId())
                .orElseThrow();
        assertThat(wallet.getPendingRelease()).isEqualTo(120_000L);

        projectWalletService.releaseToMaker(order.getProject(), settlement, 120_000L);
        wallet = projectWalletRepository.findByProjectId(order.getProject().getId()).orElseThrow();
        assertThat(wallet.getPendingRelease()).isZero();
        assertThat(wallet.getEscrowBalance()).isEqualTo(80_000L);

        // then: 거래 로그가 deposit/hold/release 순서로 남는다
        List<ProjectWalletTransaction> logs = projectWalletTransactionRepository.findAll();
        assertThat(logs).hasSize(3); // deposit + hold + release
        assertThat(logs).extracting(ProjectWalletTransaction::getType)
                .contains(ProjectWalletTransactionType.RELEASE_PENDING, ProjectWalletTransactionType.RELEASE);
    }

    private Order prepareOrder(long amount) {
        User user = userRepository.save(User.createUser(
                "user-" + UUID.randomUUID() + "@moa.com",
                "encodedPw",
                "서포터"
        ));

        Maker maker = makerRepository.save(Maker.create(user, "테스트메이커"));

        Project project = Project.builder()
                .maker(maker)
                .title("테스트 프로젝트 " + UUID.randomUUID())
                .goalAmount(500_000L)
                .build();
        projectRepository.save(project);

        Order order = Order.create(
                user,
                project,
                "ORD-" + UUID.randomUUID(),
                "테스트주문",
                amount,
                "수령인",
                "010-0000-0000",
                "서울시 어딘가",
                "101동",
                "12345"
        );
        return orderRepository.save(order);
    }

    private Settlement createSettlement(Project project, Maker maker, long grossAmount) {
        Settlement settlement = Settlement.create(
                project,
                maker,
                grossAmount,
                0L,
                0L,
                grossAmount,
                grossAmount / 2,
                grossAmount / 2
        );
        return settlementRepository.save(settlement);
    }
}
