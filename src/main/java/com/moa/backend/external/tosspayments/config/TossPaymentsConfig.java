package com.moa.backend.external.tosspayments.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.Base64;

/**
 * 토스 API 호출할 때 인증 헤더용
 */
//@Configuration
public class TossPaymentsConfig {

    //@Value("${toss.secret-key}")
    private String secretKey;

    //@Value("${toss.client-key}")
    private String clientKey;

    public static final String BASE_URL = "https://api.tosspayments.com/v1/payments";

    public HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Basic Auth: secretKey를 Base64 인코딩
        String auth = secretKey + ":";
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
        headers.set("Authorization", "Basic " + encodedAuth);

        return headers;
    }

    public String getClientKey() {
        return clientKey;
    }
}
