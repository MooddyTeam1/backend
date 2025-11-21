// src/main/java/com/example/backend/config/OpenAiProperties.java
// 한글 설명: application.yml 의 openai.* 설정을 주입받는 클래스

package com.moa.backend.global.openai;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "openai")
public class OpenAiProperties {

    // 한글 설명: OpenAI API 기본 URL
    private String baseUrl;

    // 한글 설명: OpenAI API 키
    private String apiKey;

    // 한글 설명: 사용할 모델 이름 (예: gpt-4o, gpt-4o-mini)
    private String model;
}
