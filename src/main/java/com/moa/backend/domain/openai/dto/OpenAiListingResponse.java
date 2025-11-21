// src/main/java/com/example/backend/dto/OpenAiListingResponse.java
// 한글 설명: OpenAI 가 JSON 으로 돌려줄 응답을 매핑하는 DTO
package com.moa.backend.domain.openai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class OpenAiListingResponse {

    // 한글 설명: AI가 생성한 제목
    @JsonProperty("title")
    private String title;

    // 한글 설명: 한 줄 요약
    @JsonProperty("short_description")
    private String shortDescription;

    // 한글 설명: 상세 스토리/설명
    @JsonProperty("story")
    private String story;

    // 한글 설명: 태그 리스트 (키워드)
    @JsonProperty("tags")
    private List<String> tags;

    // 한글 설명: 사람이 읽기 좋은 카테고리 이름 (예: "전자제품 > 노트북")
    @JsonProperty("category_name")
    private String categoryName;

    // 한글 설명: 백엔드에서 매핑하기 좋은 코드/슬러그 (예: "ELECTRONICS_LAPTOP")
    @JsonProperty("category_code")
    private String categoryCode;
}
