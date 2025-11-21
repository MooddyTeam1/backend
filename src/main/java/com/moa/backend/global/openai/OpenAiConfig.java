// src/main/java/com/example/backend/config/OpenAiConfig.java
// 한글 설명: OpenAI 호출용 WebClient 를 애플리케이션 공용 Bean 으로 등록

package com.moa.backend.global.openai;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
public class OpenAiConfig {

    private final OpenAiProperties properties;

    @Bean
    public WebClient openAiWebClient() {
        // 한글 설명: baseUrl, 인증 헤더 등을 공통 설정한 WebClient 생성
        return WebClient.builder()
                .baseUrl(properties.getBaseUrl())
                .defaultHeader("Authorization", "Bearer " + properties.getApiKey())
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
}
