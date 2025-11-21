// src/main/java/com/moa/backend/domain/ai/client/OpenAiListingClient.java
package com.moa.backend.domain.openai.client;

import com.moa.backend.global.openai.OpenAiProperties;
import com.moa.backend.domain.openai.dto.OpenAiListingResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Base64;

@Slf4j
@Component
@RequiredArgsConstructor
public class OpenAiListingClient {

    private final WebClient openAiWebClient;
    private final OpenAiProperties properties;
    private final ObjectMapper objectMapper;

    public OpenAiListingResponse generateFromImage(byte[] imageBytes,
                                                   String hint,
                                                   String tone) {
        try {
            String base64 = Base64.getEncoder().encodeToString(imageBytes);
            String dataUrl = "data:image/jpeg;base64," + base64;

            // ✅ 여기서 enum 값과 정확히 맞게 코드 강제
            String systemPrompt = """
                    너는 크라우드펀딩/중고거래 플랫폼에서 상품/프로젝트 설명을 작성하고,
                    적절한 카테고리를 추천하는 전문가야.
            
                    사용자가 올린 이미지를 기반으로 아래 항목을 모두 한국어로 생성해 줘:
                    - 제목(title)
                    - 한 줄 요약(short_description)
                    - 상세 스토리/설명(story)
                    - 태그 리스트(tags)
                    - 카테고리 이름(category_name)
                    - 카테고리 코드(category_code)
            
                    story 필드는 아래 마크다운 구조를 따라 작성해:
                    
                    1) 이 프로젝트를 준비하게 된 배경과 펀딩 목적
                       - 왜 이 프로젝트를 시작하게 되었는지
                       - 어떤 문제/불편/아쉬움을 보고 만들게 되었는지
                       - 펀딩금을 모아서 무엇을 하려는지
                    
                    2) 이번에 선보이는 프로젝트의 이야기
                       - 이번 프로젝트/제품/서비스가 어떤 이야기와 컨셉을 가지고 있는지
                       - 리워드(제품/서비스) 사진을 보여 주듯이, 사용자가 받게 될 모습을 떠올리며 설명
                    
                    3) 프로젝트 혹은 제품/서비스의 차별점 (핵심 3가지 위주)
                       - 기존 것들과 비교했을 때 다른 점
                       - 주요 장점을 3가지 정도로 정리
                    
                    그리고 실제 story 출력은 아래 마크다운 섹션 구조를 지켜서 작성해라:
            
                    ## 소개
                    프로젝트 혹은 제품의 핵심 가치를 한 문단으로 정리해 줘.
                    (이 프로젝트가 해결하려는 문제, 핵심 아이디어, 서포터가 얻는 가치를 포함)
            
                    ## 특징
                    - 주요 장점 1: 한 줄 요약 + 1~2문장 보충 설명
                    - 주요 장점 2: 한 줄 요약 + 1~2문장 보충 설명
                    - 주요 장점 3: 한 줄 요약 + 1~2문장 보충 설명
            
                    ## 제작·배송 계획
                    제작 일정, 검수 절차, 배송 계획을
                    "펀딩 종료 → 제작 → 검수 → 포장 → 배송" 과 같은 단계별 흐름으로 작성해 줘.
            
                    ## 위험 요소와 대응
                    발생 가능한 리스크(제작 지연, 원자재 수급 문제 등)와
                    각 리스크에 대한 대응 방안을 솔직하게 안내해 줘.
            
                    category_name:
                      - 사람이 읽기 쉬운 형태 (예: "테크", "디자인", "식품", "패션/잡화", "뷰티",
                        "홈리빙", "게임", "아트/공예", "출판/콘텐츠")
            
                    category_code:
                      - 아래 값 중 하나만 사용해야 한다. 반드시 정확히 일치하는 대문자 문자열을 사용해라.
                        - TECH
                        - DESIGN
                        - FOOD
                        - FASHION
                        - BEAUTY
                        - HOME_LIVING
                        - GAME
                        - ART
                        - PUBLISH
            
                    조건:
                    - 존댓말 사용
                    - 너무 과장되거나 광고성 문구는 피하고, 실제 판매/펀딩에 도움이 되는 정보 중심으로 작성
                    - story 필드는 마크다운 형식으로 작성
                    - JSON 형식 외의 텍스트는 절대 포함하지 말 것
            
                    반드시 아래 JSON 형식으로만 응답해라:
                    {
                      "title": "문자열",
                      "short_description": "문자열",
                      "story": "문자열 (마크다운 섹션 포함)",
                      "tags": ["문자열", "문자열"],
                      "category_name": "문자열",
                      "category_code": "문자열"
                    }
                    """;

            String userPrompt = """
                    사용자가 업로드한 이미지는 상품 또는 프로젝트의 대표 이미지야.
                    아래 추가 힌트와 원하는 톤을 참고해서 위 JSON 구조에 맞게 작성해 줘.

                    - 추가 힌트(선택): %s
                    - 원하는 톤(선택): %s
                    """.formatted(
                    hint != null && !hint.isBlank() ? hint : "없음",
                    tone != null && !tone.isBlank() ? tone : "기본 (친근한 존댓말)"
            );

            ObjectNode requestBody = objectMapper.createObjectNode();
            requestBody.put("model", properties.getModel());
            requestBody.put("temperature", 0.7);

            var messages = requestBody.putArray("messages");

            ObjectNode systemMessage = messages.addObject();
            systemMessage.put("role", "system");
            var systemContent = systemMessage.putArray("content");
            systemContent.addObject()
                    .put("type", "text")
                    .put("text", systemPrompt);

            ObjectNode userMessage = messages.addObject();
            userMessage.put("role", "user");
            var userContent = userMessage.putArray("content");
            userContent.addObject()
                    .put("type", "text")
                    .put("text", userPrompt);
            ObjectNode imageContent = userContent.addObject();
            imageContent.put("type", "image_url");
            ObjectNode imageUrl = imageContent.putObject("image_url");
            imageUrl.put("url", dataUrl);

            ObjectNode responseFormat = requestBody.putObject("response_format");
            responseFormat.put("type", "json_object");

            log.info("OpenAI Listing Request: {}", objectMapper.writeValueAsString(requestBody));

            String responseJson = openAiWebClient.post()
                    .uri("/v1/chat/completions")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .onErrorResume(ex -> {
                        log.error("OpenAI 호출 실패", ex);
                        return Mono.error(new RuntimeException("OpenAI 호출 중 오류가 발생했습니다."));
                    })
                    .block();

            log.info("OpenAI Listing Raw Response: {}", responseJson);

            JsonNode root = objectMapper.readTree(responseJson);
            JsonNode choices = root.path("choices");
            if (!choices.isArray() || choices.isEmpty()) {
                throw new RuntimeException("OpenAI 응답에 choices 항목이 없습니다.");
            }

            String content = choices.get(0)
                    .path("message")
                    .path("content")
                    .asText();

            log.info("OpenAI Listing Content(JSON): {}", content);

            return objectMapper.readValue(content, OpenAiListingResponse.class);

        } catch (Exception e) {
            log.error("OpenAI Listing 호출/파싱 중 오류", e);
            throw new RuntimeException("AI 설명 생성에 실패했습니다: " + e.getMessage(), e);
        }
    }
}
