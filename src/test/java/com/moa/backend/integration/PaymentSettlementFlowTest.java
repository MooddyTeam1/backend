package com.moa.backend.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

import com.moa.backend.domain.maker.entity.Maker;
import com.moa.backend.domain.maker.repository.MakerRepository;
import com.moa.backend.domain.order.entity.Order;
import com.moa.backend.domain.order.repository.OrderRepository;
import com.moa.backend.domain.payment.entity.Payment;
import com.moa.backend.domain.payment.repository.PaymentRepository;
import com.moa.backend.domain.payment.service.PaymentService;
import com.moa.backend.domain.project.entity.Project;
import com.moa.backend.domain.project.repository.ProjectRepository;
import com.moa.backend.domain.settlement.entity.Settlement;
import com.moa.backend.domain.settlement.entity.SettlementPayoutStatus;
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
import com.moa.backend.external.tosspayments.TossPaymentsClient;
import com.moa.backend.external.tosspayments.dto.TossCancelRequest;
import com.moa.backend.external.tosspayments.dto.TossCancelResponse;
import com.moa.backend.external.tosspayments.dto.TossConfirmRequest;
import com.moa.backend.external.tosspayments.dto.TossConfirmResponse;
import com.moa.backend.global.util.MoneyCalculator;
import jakarta.transaction.Transactional;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@Import(PaymentSettlementFlowTest.MockConfig.class)
class PaymentSettlementFlowTest {

    @Autowired private PaymentService paymentService;
    @Autowired private SettlementService settlementService;
    @Autowired private SettlementRepository settlementRepository;
    @Autowired private OrderRepository orderRepository;
    @Autowired private PaymentRepository paymentRepository;
    @Autowired private ProjectWalletRepository projectWalletRepository;
    @Autowired private ProjectWalletService projectWalletService;
    @Autowired private PlatformWalletRepository platformWalletRepository;
    @Autowired private MakerWalletRepository makerWalletRepository;
    @Autowired private MakerWalletService makerWalletService;
    @Autowired private UserRepository userRepository;
    @Autowired private MakerRepository makerRepository;
    @Autowired private ProjectRepository projectRepository;
    @Autowired private TossPaymentsClient tossPaymentsClient;

    @Test
    @DisplayName("결제 → 정산(선지급/잔금) 전체 플로우를 검증한다")
    void paymentToSettlementFlow() {
        // given: 주문/프로젝트/메이커 생성
        TestContext ctx = prepareOrder(150_000L);
        mockConfirmResponse(ctx.order.getOrderCode(), ctx.order.getTotalAmount());

        // when: 결제 승인
        Payment payment = paymentService.confirmPayment(
                "tosspay-flow",
                ctx.order.getOrderCode(),
                ctx.order.getTotalAmount()
        );

        // then: Payment/Order/ProjectWallet/PlatformWallet 업데이트 확인
        assertThat(payment.getId()).isNotNull();
        ProjectWallet projectWallet = projectWalletRepository.findByProjectId(ctx.project.getId())
                .orElseThrow();
        PlatformWallet platformWallet = platformWalletRepository.findTopByOrderByIdAsc()
                .orElseThrow();
        long expectedAfterPg = calculateAfterPg(ctx.order.getTotalAmount());
        assertThat(projectWallet.getEscrowBalance()).isEqualTo(ctx.order.getTotalAmount());
        assertThat(platformWallet.getTotalBalance()).isEqualTo(expectedAfterPg);

        // when: 정산 생성 및 선지급
        Settlement settlement = settlementService.createSettlement(ctx.project.getId());
        settlementService.payFirstPayout(settlement.getId());
        settlement = settlementRepository.findById(settlement.getId()).orElseThrow();

        MakerWallet makerWallet = makerWalletRepository.findByMakerId(ctx.maker.getId())
                .orElseThrow();
        assertThat(settlement.getStatus()).isEqualTo(SettlementStatus.FIRST_PAID);
        assertThat(makerWallet.getAvailableBalance()).isEqualTo(settlement.getFirstPaymentAmount());
        assertThat(makerWallet.getPendingBalance()).isEqualTo(settlement.getFinalPaymentAmount());

        // when: 잔금 준비 → 잔금 지급
        settlement.markFinalReady();
        settlementRepository.save(settlement);
        settlementService.payFinalPayout(settlement.getId());
        settlement = settlementRepository.findById(settlement.getId()).orElseThrow();
        makerWallet = makerWalletRepository.findByMakerId(ctx.maker.getId()).orElseThrow();
        projectWallet = projectWalletRepository.findByProjectId(ctx.project.getId()).orElseThrow();

        // then: MakerWallet은 netAmount 전액 보유, Escrow엔 수수료만 잔존, Settlement는 COMPLETED
        long expectedFeeHold = ctx.order.getTotalAmount() - settlement.getNetAmount();
        assertThat(makerWallet.getAvailableBalance()).isEqualTo(settlement.getNetAmount());
        assertThat(makerWallet.getPendingBalance()).isZero();
        assertThat(projectWallet.getEscrowBalance()).isEqualTo(expectedFeeHold);
        assertThat(settlement.getStatus()).isEqualTo(SettlementStatus.COMPLETED);
        assertThat(settlement.getFinalPaymentStatus()).isEqualTo(SettlementPayoutStatus.DONE);
    }

    private TestContext prepareOrder(long amount) {
        User user = userRepository.save(User.createUser(
                "flow-" + UUID.randomUUID() + "@moa.com",
                "encodedPw",
                "흐름테스터"
        ));
        Maker maker = makerRepository.save(Maker.create(user, "흐름메이커"));
        Project project = projectRepository.save(Project.builder()
                .maker(maker)
                .title("플로우 프로젝트")
                .goalAmount(1_000_000L)
                .build());
        Order order = orderRepository.save(Order.create(
                user,
                project,
                "ORD-" + UUID.randomUUID(),
                "플로우주문",
                amount,
                "수령인",
                "010-0000-1111",
                "서울특별시",
                "101동",
                "11111"
        ));

        projectWalletService.createForProject(project);
        makerWalletService.createForMaker(maker);
        PlatformWallet wallet = platformWalletRepository.findTopByOrderByIdAsc()
                .orElseGet(() -> platformWalletRepository.save(PlatformWallet.initialize()));
        wallet.deposit(0L);
        platformWalletRepository.save(wallet);

        return new TestContext(order, project, maker);
    }

    private long calculateAfterPg(long totalAmount) {
        long pgFee = MoneyCalculator.percentageOf(totalAmount, 0.05);
        return MoneyCalculator.subtract(totalAmount, pgFee);
    }

    private void mockConfirmResponse(String orderCode, long amount) {
        TossConfirmResponse response = Mockito.mock(TossConfirmResponse.class);
        TossConfirmResponse.CardInfo cardInfo = Mockito.mock(TossConfirmResponse.CardInfo.class);
        TossConfirmResponse.ReceiptInfo receiptInfo = Mockito.mock(TossConfirmResponse.ReceiptInfo.class);
        Mockito.when(response.getPaymentKey()).thenReturn("tosspay-" + orderCode);
        Mockito.when(response.getTotalAmount()).thenReturn(amount);
        Mockito.when(response.getMethod()).thenReturn("CARD");
        Mockito.when(response.getApprovedAt()).thenReturn(OffsetDateTime.now());
        Mockito.when(response.getCard()).thenReturn(cardInfo);
        Mockito.when(cardInfo.getNumber()).thenReturn("1111-****-****-2222");
        Mockito.when(response.getReceipt()).thenReturn(receiptInfo);
        Mockito.when(receiptInfo.getUrl()).thenReturn("https://receipt.moa/test");
        Mockito.when(tossPaymentsClient.confirmPayment(any(TossConfirmRequest.class))).thenReturn(response);
        Mockito.when(tossPaymentsClient.cancelPayment(any(), any(TossCancelRequest.class)))
                .thenReturn(Mockito.mock(TossCancelResponse.class));
    }

    private record TestContext(Order order, Project project, Maker maker) {}

    @TestConfiguration
    static class MockConfig {
        @Bean
        TossPaymentsClient tossPaymentsClient() {
            return Mockito.mock(TossPaymentsClient.class);
        }
    }
}
