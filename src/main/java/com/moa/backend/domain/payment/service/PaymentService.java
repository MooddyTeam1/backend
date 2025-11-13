package com.moa.backend.domain.payment.service;

import com.moa.backend.domain.order.entity.Order;
import com.moa.backend.domain.order.repository.OrderRepository;
import com.moa.backend.domain.payment.entity.Payment;
import com.moa.backend.domain.payment.entity.PaymentStatus;
import com.moa.backend.domain.payment.repository.PaymentRepository;
import com.moa.backend.external.tosspayments.TossPaymentsClient;
import com.moa.backend.external.tosspayments.dto.TossCancelRequest;
import com.moa.backend.external.tosspayments.dto.TossCancelResponse;
import com.moa.backend.external.tosspayments.dto.TossConfirmRequest;
import com.moa.backend.external.tosspayments.dto.TossConfirmResponse;
import com.moa.backend.global.error.AppException;
import com.moa.backend.global.error.ErrorCode;
import com.moa.backend.global.util.MoneyCalculator;
import com.moa.backend.domain.wallet.service.PlatformWalletService;
import com.moa.backend.domain.wallet.service.ProjectWalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final TossPaymentsClient tossClient;
    private final ProjectWalletService projectWalletService;
    private final PlatformWalletService platformWalletService;

    /**
     * 결제 승인 처리
     */
    @Transactional
    public Payment confirmPayment(String paymentKey, String orderCode, Long amount) {
        // 1. 주문 조회
        Order order = orderRepository.findByOrderCode(orderCode)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND, "주문을 찾을 수 없습니다: " + orderCode));

        // 2. 금액 검증
        if (!order.getTotalAmount().equals(amount)) {
            throw new AppException(ErrorCode.PAYMENT_AMOUNT_MISMATCH,
                    "결제 금액이 주문 금액과 일치하지 않습니다. 주문금액=" + order.getTotalAmount() + ", 요청금액=" + amount);
        }

        // 3. 중복 승인 체크
        if (paymentRepository.existsByOrder(order)) {
            throw new AppException(ErrorCode.PAYMENT_ALREADY_APPROVED, "이미 승인된 주문입니다: " + orderCode);
        }

        // 4. 토스 승인 API 호출
        TossConfirmRequest tossRequest = TossConfirmRequest.builder()
                .paymentKey(paymentKey)
                .orderId(orderCode)
                .amount(amount)
                .build();

        TossConfirmResponse tossResponse = tossClient.confirmPayment(tossRequest);

        // 5. Payment 엔티티 생성
        Payment payment = Payment.builder()
                .order(order)
                .paymentKey(tossResponse.getPaymentKey())
                .amount(tossResponse.getTotalAmount())
                .method(tossResponse.getMethod())
                .status(PaymentStatus.DONE)
                .approvedAt(tossResponse.getApprovedAt())
                .cardMasked(extractCardNumber(tossResponse))
                .receiptUrl(extractReceiptUrl(tossResponse))
                .build();

        paymentRepository.save(payment);

        // 6. 주문 상태 업데이트
        order.markPaid();
        orderRepository.save(order);

        // Wallet 동기화
        Long grossAmount = payment.getAmount();
        Long pgFee = MoneyCalculator.percentageOf(grossAmount, 0.05);
        Long netAmount = MoneyCalculator.subtract(grossAmount, pgFee);

        projectWalletService.deposit(order.getProject().getId(), grossAmount, order);
        platformWalletService.deposit(netAmount, payment);

        log.info("결제 승인 완료 + Wallet 연동: orderCode={}, paymentId={}, gross={}, net={}",
                orderCode, payment.getId(), grossAmount, netAmount);
        return payment;
    }

    /**
     * 결제 취소 처리
     */
    @Transactional
    public void cancelPayment(Long paymentId, String reason) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_NOT_FOUND,
                        "결제를 찾을 수 없습니다: " + paymentId));
        executeCancel(payment, payment.getOrder(), reason);
    }

    /**
     * 주문 객체만 가지고 있는 배치/서비스에서 사용할 수 있는 취소 API.
     */
    @Transactional
    public void cancelByOrder(Order order, String reason) {
        Payment payment = paymentRepository.findByOrder(order)
                .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_NOT_FOUND,
                        "주문에 연결된 결제를 찾을 수 없습니다: orderId=" + order.getId()));
        executeCancel(payment, order, reason);
    }

    // Helper methods
    private String extractCardNumber(TossConfirmResponse response) {
        if (response.getCard() != null) {
            return response.getCard().getNumber();
        }
        return null;
    }

    private String extractReceiptUrl(TossConfirmResponse response) {
        if (response.getReceipt() != null) {
            return response.getReceipt().getUrl();
        }
        return null;
    }

    /**
     * cancelPayment/cancelByOrder가 공유하는 실질 취소 처리 로직.
     */
    private void executeCancel(Payment payment, Order order, String reason) {
        if (payment.getStatus() == PaymentStatus.CANCELED) {
            log.warn("이미 취소된 결제: paymentId={}", payment.getId());
            return;
        }

        TossCancelRequest tossRequest = TossCancelRequest.builder()
                .cancelReason(reason)
                .cancelAmount(null)
                .build();
        tossClient.cancelPayment(payment.getPaymentKey(), tossRequest);

        payment.cancel();
        paymentRepository.save(payment);

        order.cancel();
        orderRepository.save(order);

        Long grossAmount = payment.getAmount();
        Long pgFee = MoneyCalculator.percentageOf(grossAmount, 0.05);
        Long netAmount = MoneyCalculator.subtract(grossAmount, pgFee);

        projectWalletService.refund(order.getProject().getId(), grossAmount, order);
        platformWalletService.recordRefund(payment, netAmount);

        log.info("결제 취소 완료 + Wallet 연동: paymentId={}, reason={}, gross={}, net={}",
                payment.getId(), reason, grossAmount, netAmount);
    }
}
