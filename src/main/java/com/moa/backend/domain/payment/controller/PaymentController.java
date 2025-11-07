package com.moa.backend.domain.payment.controller;

import com.moa.backend.domain.payment.dto.ConfirmPaymentRequest;
import com.moa.backend.domain.payment.dto.ConfirmPaymentResponse;
import com.moa.backend.domain.payment.entity.Payment;
import com.moa.backend.domain.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * 결제 승인 API
     * 토스페이먼츠에서 결제 후 successUrl로 리다이렉트될 때
     * 프론트엔드가 이 API를 호출해서 결제를 승인합니다.
     */
    @PostMapping("/confirm")
    public ResponseEntity<ConfirmPaymentResponse> confirmPayment(
            @RequestBody ConfirmPaymentRequest request
    ) {
        Payment payment = paymentService.confirmPayment(
                request.getPaymentKey(),
                request.getOrderId(),
                request.getAmount()
        );

        ConfirmPaymentResponse response = ConfirmPaymentResponse.from(payment);
        return ResponseEntity.ok(response);
    }

    /**
     * 결제 취소 API
     * 관리자 또는 사용자가 결제를 취소할 때 사용합니다.
     */
    @PostMapping("/{paymentId}/cancel")
    public ResponseEntity<Void> cancelPayment(
            @PathVariable Long paymentId,
            @RequestParam(required = false, defaultValue = "사용자 요청") String reason
    ) {
        paymentService.cancelPayment(paymentId, reason);
        return ResponseEntity.ok().build();
    }
}
