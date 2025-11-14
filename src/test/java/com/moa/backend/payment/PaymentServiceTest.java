package com.moa.backend.payment;

import com.moa.backend.domain.maker.entity.Maker;
import com.moa.backend.domain.maker.repository.MakerRepository;
import com.moa.backend.domain.order.entity.Order;
import com.moa.backend.domain.order.entity.OrderStatus;
import com.moa.backend.domain.order.repository.OrderRepository;
import com.moa.backend.domain.payment.entity.Payment;
import com.moa.backend.domain.payment.entity.PaymentStatus;
import com.moa.backend.domain.payment.repository.PaymentRepository;
import com.moa.backend.domain.payment.service.PaymentService;
import com.moa.backend.domain.project.entity.Project;
import com.moa.backend.domain.project.repository.ProjectRepository;
import com.moa.backend.domain.user.entity.User;
import com.moa.backend.domain.user.repository.UserRepository;
import com.moa.backend.domain.wallet.entity.PlatformWallet;
import com.moa.backend.domain.wallet.entity.ProjectWallet;
import com.moa.backend.domain.wallet.repository.PlatformWalletRepository;
import com.moa.backend.domain.wallet.repository.ProjectWalletRepository;
import com.moa.backend.domain.wallet.service.ProjectWalletService;
import com.moa.backend.external.tosspayments.TossPaymentsClient;
import com.moa.backend.external.tosspayments.dto.TossCancelRequest;
import com.moa.backend.external.tosspayments.dto.TossCancelResponse;
import com.moa.backend.external.tosspayments.dto.TossConfirmRequest;
import com.moa.backend.external.tosspayments.dto.TossConfirmResponse;
import com.moa.backend.global.util.MoneyCalculator;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@Import(PaymentServiceTest.MockConfig.class)
class PaymentServiceTest {

    @Autowired
    private PaymentService paymentService;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MakerRepository makerRepository;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private ProjectWalletService projectWalletService;
    @Autowired
    private ProjectWalletRepository projectWalletRepository;
    @Autowired
    private PlatformWalletRepository platformWalletRepository;

    @Autowired
    private TossPaymentsClient tossPaymentsClient;

    @Test
    @DisplayName("결제 승인 시 Payment/Order/Wallet이 함께 업데이트된다")
    void confirmPayment_updatesWallets() {
        // given: 결제 대상 주문과 Toss 승인 응답 mock(총액 120,000원)
        PaymentTestContext context = prepareOrder();
        mockConfirmResponse(context.order());

        // when: 결제 승인을 호출해 Payment/Order를 완료 상태로 만든다
        Payment payment = paymentService.confirmPayment(
                "tosspay_123",
                context.order().getOrderCode(),
                context.order().getTotalAmount()
        );

        // then: ProjectWallet은 총액, PlatformWallet은 PG 5% 수수료 제외 금액(95%)이 적립된다
        ProjectWallet projectWallet = projectWalletRepository.findByProjectId(context.project().getId())
                .orElseThrow();
        PlatformWallet platformWallet = platformWalletRepository.findTopByOrderByIdAsc()
                .orElseThrow();
        long expectedPlatformBalance = calculateAfterPgAmount(context.order().getTotalAmount());

        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.DONE);
        assertThat(paymentRepository.existsById(payment.getId())).isTrue();
        assertThat(payment.getOrder().getStatus()).isEqualTo(OrderStatus.PAID);
        assertThat(projectWallet.getEscrowBalance()).isEqualTo(context.order().getTotalAmount());
        assertThat(platformWallet.getTotalBalance()).isEqualTo(expectedPlatformBalance);
    }

    @Test
    @DisplayName("결제 취소 시 Payment/Order/Wallet 상태가 되돌아간다")
    void cancelPayment_updatesWallets() {
        // given: 결제 완료 상태인 주문과 Toss 취소 응답 mock
        PaymentTestContext context = prepareOrder();
        mockConfirmResponse(context.order());

        Payment payment = paymentService.confirmPayment(
                "tosspay_456",
                context.order().getOrderCode(),
                context.order().getTotalAmount()
        );

        // when: 결제를 취소
        mockCancelResponse();
        paymentService.cancelPayment(payment.getId(), "테스트 취소");

        // then: Payment/Order 상태는 CANCELED, 두 Wallet의 잔액은 모두 0으로 복구된다
        Payment canceledPayment = paymentRepository.findById(payment.getId()).orElseThrow();
        ProjectWallet projectWallet = projectWalletRepository.findByProjectId(context.project().getId())
                .orElseThrow();
        PlatformWallet platformWallet = platformWalletRepository.findTopByOrderByIdAsc()
                .orElseThrow();

        assertThat(canceledPayment.getStatus()).isEqualTo(PaymentStatus.CANCELED);
        assertThat(canceledPayment.getOrder().getStatus()).isEqualTo(OrderStatus.CANCELED);
        assertThat(projectWallet.getEscrowBalance()).isZero();
        assertThat(platformWallet.getTotalBalance()).isZero();
    }

    private PaymentTestContext prepareOrder() {
        User user = userRepository.save(User.createUser(
                "payment-test-" + UUID.randomUUID() + "@moa.com",
                "encodedPw",
                "결제테스터"
        ));
        Maker maker = makerRepository.save(Maker.create(user, "결제테스트메이커"));
        Project project = projectRepository.save(Project.builder()
                .maker(maker)
                .title("결제 테스트 프로젝트")
                .goalAmount(500_000L)
                .build());

        Order order = orderRepository.save(Order.create(
                user,
                project,
                "ORD-" + UUID.randomUUID(),
                "결제테스트",
                120_000L,
                "수령인",
                "010-0000-0000",
                "서울시",
                "101동",
                "12345"
        ));

        projectWalletService.createForProject(project);
        platformWalletRepository.findTopByOrderByIdAsc()
                .orElseGet(() -> platformWalletRepository.save(PlatformWallet.initialize()));

        return new PaymentTestContext(order, project);
    }

    private void mockConfirmResponse(Order order) {
        TossConfirmResponse response = mock(TossConfirmResponse.class);
        TossConfirmResponse.CardInfo cardInfo = mock(TossConfirmResponse.CardInfo.class);
        TossConfirmResponse.ReceiptInfo receiptInfo = mock(TossConfirmResponse.ReceiptInfo.class);

        when(response.getPaymentKey()).thenReturn("tosspay-" + UUID.randomUUID());
        when(response.getTotalAmount()).thenReturn(order.getTotalAmount());
        when(response.getMethod()).thenReturn("CARD");
        when(response.getApprovedAt()).thenReturn(LocalDateTime.now());
        when(response.getCard()).thenReturn(cardInfo);
        when(cardInfo.getNumber()).thenReturn("1234-****-****-5678");
        when(response.getReceipt()).thenReturn(receiptInfo);
        when(receiptInfo.getUrl()).thenReturn("https://receipt.test");

        when(tossPaymentsClient.confirmPayment(any(TossConfirmRequest.class))).thenReturn(response);
    }

    private void mockCancelResponse() {
        TossCancelResponse response = mock(TossCancelResponse.class);
        when(tossPaymentsClient.cancelPayment(any(), any(TossCancelRequest.class))).thenReturn(response);
    }

    private long calculateAfterPgAmount(long totalAmount) {
        long pgFee = MoneyCalculator.percentageOf(totalAmount, 0.05);
        return MoneyCalculator.subtract(totalAmount, pgFee);
    }

    private record PaymentTestContext(Order order, Project project) {
    }

    @TestConfiguration
    static class MockConfig {
        @Bean
        TossPaymentsClient tossPaymentsClient() {
            return Mockito.mock(TossPaymentsClient.class);
        }
    }
}
