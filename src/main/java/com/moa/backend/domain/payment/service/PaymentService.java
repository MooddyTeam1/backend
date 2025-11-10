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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

//@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final TossPaymentsClient tossClient;

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

        log.info("결제 승인 완료: orderCode={}, paymentId={}", orderCode, payment.getId());
        return payment;
    }

    /**
     * 결제 취소 처리
     */
    @Transactional
    public void cancelPayment(Long paymentId, String reason) {
        // 1. Payment 조회
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_NOT_FOUND, "결제를 찾을 수 없습니다: " + paymentId));

        // 2. 이미 취소되었는지 확인
        if (payment.getStatus() == PaymentStatus.CANCELED) {
            log.warn("이미 취소된 결제: paymentId={}", paymentId);
            return;
        }

        // 3. 토스 취소 API 호출
        TossCancelRequest tossRequest = TossCancelRequest.builder()
                .cancelReason(reason)
                .cancelAmount(null)  // 전액 취소
                .build();

        TossCancelResponse tossResponse = tossClient.cancelPayment(
                payment.getPaymentKey(),
                tossRequest
        );

        // 4. Payment 상태 업데이트
        payment.cancel();
        paymentRepository.save(payment);

        // 5. Order 상태 업데이트
        Order order = payment.getOrder();
        order.cancel();
        orderRepository.save(order);

        log.info("결제 취소 완료: paymentId={}, reason={}", paymentId, reason);
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
}
