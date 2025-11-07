package com.moa.backend.external.tosspayments;

import com.moa.backend.external.tosspayments.config.TossPaymentsConfig;
import com.moa.backend.external.tosspayments.dto.TossCancelRequest;
import com.moa.backend.external.tosspayments.dto.TossCancelResponse;
import com.moa.backend.external.tosspayments.dto.TossConfirmRequest;
import com.moa.backend.external.tosspayments.dto.TossConfirmResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

//@Component
@RequiredArgsConstructor
@Slf4j
public class TossPaymentsClient {

    private final RestTemplate restTemplate;
    private final TossPaymentsConfig tossConfig;

    /**
     * 결제 승인 API 호출
     */
    public TossConfirmResponse confirmPayment(TossConfirmRequest request) {
        String url = TossPaymentsConfig.BASE_URL + "/confirm";

        HttpHeaders headers = tossConfig.createHeaders();
        HttpEntity<TossConfirmRequest> entity = new HttpEntity<>(request, headers);

        log.info("토스 승인 API 호출: orderId={}, amount={}",
                request.getOrderId(), request.getAmount());

        try {
            ResponseEntity<TossConfirmResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    TossConfirmResponse.class
            );

            log.info("토스 승인 성공: paymentKey={}", response.getBody().getPaymentKey());
            return response.getBody();

        } catch (HttpClientErrorException e) {
            log.error("토스 승인 실패: {}", e.getResponseBodyAsString());
            throw new RuntimeException("결제 승인 실패: " + e.getResponseBodyAsString());
        }
    }

    /**
     * 결제 취소 API 호출
     */
    public TossCancelResponse cancelPayment(String paymentKey, TossCancelRequest request) {
        String url = TossPaymentsConfig.BASE_URL + "/" + paymentKey + "/cancel";

        HttpHeaders headers = tossConfig.createHeaders();
        HttpEntity<TossCancelRequest> entity = new HttpEntity<>(request, headers);

        log.info("토스 취소 API 호출: paymentKey={}, reason={}",
                paymentKey, request.getCancelReason());

        try {
            ResponseEntity<TossCancelResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    TossCancelResponse.class
            );

            log.info("토스 취소 성공: paymentKey={}", paymentKey);
            return response.getBody();

        } catch (HttpClientErrorException e) {
            log.error("토스 취소 실패: {}", e.getResponseBodyAsString());
            throw new RuntimeException("결제 취소 실패: " + e.getResponseBodyAsString());
        }
    }
}
