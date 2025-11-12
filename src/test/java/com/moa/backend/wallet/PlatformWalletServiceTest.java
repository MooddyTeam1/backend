package com.moa.backend.wallet;

import static org.assertj.core.api.Assertions.assertThat;

import com.moa.backend.domain.maker.entity.Maker;
import com.moa.backend.domain.maker.repository.MakerRepository;
import com.moa.backend.domain.order.entity.Order;
import com.moa.backend.domain.order.repository.OrderRepository;
import com.moa.backend.domain.payment.entity.Payment;
import com.moa.backend.domain.payment.entity.PaymentStatus;
import com.moa.backend.domain.payment.repository.PaymentRepository;
import com.moa.backend.domain.project.entity.Project;
import com.moa.backend.domain.project.repository.ProjectRepository;
import com.moa.backend.domain.settlement.entity.Settlement;
import com.moa.backend.domain.settlement.repository.SettlementRepository;
import com.moa.backend.domain.user.entity.User;
import com.moa.backend.domain.user.repository.UserRepository;
import com.moa.backend.domain.wallet.entity.PlatformWallet;
import com.moa.backend.domain.wallet.entity.PlatformWalletTransaction;
import com.moa.backend.domain.wallet.entity.PlatformWalletTransactionType;
import com.moa.backend.domain.wallet.repository.PlatformWalletRepository;
import com.moa.backend.domain.wallet.repository.PlatformWalletTransactionRepository;
import com.moa.backend.domain.wallet.service.PlatformWalletService;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class PlatformWalletServiceTest {

    @Autowired
    private PlatformWalletService platformWalletService;
    @Autowired
    private PlatformWalletRepository platformWalletRepository;
    @Autowired
    private PlatformWalletTransactionRepository platformWalletTransactionRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MakerRepository makerRepository;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private SettlementRepository settlementRepository;

    @Test
    void deposit_플랫폼지갑생성_및_로그기록() {
        // given: 결제 데이터 준비 (PG 순입금 140,000 가정)
        Payment payment = preparePayment(150_000L);

        // when: 플랫폼 지갑에 입금 반영
        platformWalletService.deposit(140_000L, payment);

        // then: 잔액과 거래 로그가 기대값과 일치
        PlatformWallet wallet = platformWalletRepository.findTopByOrderByIdAsc().orElseThrow();
        assertThat(wallet.getTotalBalance()).isEqualTo(140_000L);
        assertThat(wallet.getTotalProjectDeposit()).isEqualTo(140_000L);

        List<PlatformWalletTransaction> logs = platformWalletTransactionRepository.findAll();
        assertThat(logs).hasSize(1);
        PlatformWalletTransaction tx = logs.get(0);
        assertThat(tx.getType()).isEqualTo(PlatformWalletTransactionType.PAYMENT_DEPOSIT);
        assertThat(tx.getAmount()).isEqualTo(140_000L);
    }

    @Test
    void 메이커송금과_환불_flow() {
        // given: 지갑과 정산 데이터 준비
        Payment payment = preparePayment(200_000L);
        platformWalletService.deposit(190_000L, payment);

        Settlement settlement = createSettlement(payment.getOrder().getProject(), payment.getOrder().getProject().getMaker());
        // when: 메이커 송금 → 환불 순으로 처리
        platformWalletService.recordMakerWithdrawal(settlement, 90_000L);

        PlatformWallet wallet = platformWalletRepository.findTopByOrderByIdAsc().orElseThrow();
        assertThat(wallet.getTotalBalance()).isEqualTo(100_000L);
        assertThat(wallet.getTotalMakerPayout()).isEqualTo(90_000L);

        platformWalletService.recordRefund(payment, 30_000L);

        wallet = platformWalletRepository.findTopByOrderByIdAsc().orElseThrow();
        assertThat(wallet.getTotalBalance()).isEqualTo(70_000L);

        // then: 세 가지 거래 로그가 순서대로 존재
        List<PlatformWalletTransaction> logs = platformWalletTransactionRepository.findAll()
                .stream()
                .sorted((a, b) -> Long.compare(a.getId(), b.getId()))
                .toList();
        assertThat(logs).hasSize(3);
        assertThat(logs).extracting(PlatformWalletTransaction::getType)
                .containsExactly(
                        PlatformWalletTransactionType.PAYMENT_DEPOSIT,
                        PlatformWalletTransactionType.WITHDRAW_TO_MAKER,
                        PlatformWalletTransactionType.REFUND_OUT
                );
    }

    private Payment preparePayment(long orderAmount) {
        User user = userRepository.save(User.createUser(
                "platform-" + UUID.randomUUID() + "@moa.com",
                "encodedPw",
                "플랫폼서포터"
        ));
        Maker maker = makerRepository.save(Maker.create(user, "플랫폼메이커"));
        Project project = projectRepository.save(Project.builder()
                .maker(maker)
                .title("플랫폼 프로젝트 " + UUID.randomUUID())
                .goalAmount(1_000_000L)
                .build());
        Order order = orderRepository.save(Order.create(
                user,
                project,
                "ORD-" + UUID.randomUUID(),
                "플랫폼주문",
                orderAmount,
                "수령인",
                "010-1111-2222",
                "서울시 플랫폼구",
                "상세주소",
                "54321"
        ));
        Payment payment = Payment.builder()
                .order(order)
                .paymentKey("pay-" + UUID.randomUUID())
                .amount(orderAmount)
                .method("CARD")
                .status(PaymentStatus.DONE)
                .approvedAt(LocalDateTime.now())
                .cardMasked("1111-****-****-2222")
                .receiptUrl("https://receipt.example.com")
                .build();
        return paymentRepository.save(payment);
    }

    private Settlement createSettlement(Project project, Maker maker) {
        Settlement settlement = Settlement.create(
                project,
                maker,
                200_000L,
                10_000L,
                10_000L,
                180_000L,
                90_000L,
                90_000L
        );
        return settlementRepository.save(settlement);
    }
}
