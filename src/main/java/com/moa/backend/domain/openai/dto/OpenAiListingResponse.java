// src/main/java/com/example/backend/dto/OpenAiListingResponse.java
// 한글 설명: OpenAI 가 JSON 으로 돌려줄 응답을 매핑하는 DTO
package com.moa.backend.domain.openai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "OpenAI 생성 결과(내부 매핑용)")
public class OpenAiListingResponse {

    // 한글 설명: AI가 생성한 제목
    @Schema(description = "AI 생성 제목", example = "휴대용 솔라 백팩")
    @JsonProperty("title")
    private String title;

    // 한글 설명: 한 줄 요약
    @Schema(description = "한 줄 요약", example = "태양광 충전과 LTE 트래커를 내장한 백팩")
    @JsonProperty("short_description")
    private String shortDescription;

    // 한글 설명: 상세 스토리/설명
    @Schema(description = "스토리/설명", example = "### 왜 만들었나\n여행자와 하이커를 위해...")
    @JsonProperty("story")
    private String story;

    // 한글 설명: 태그 리스트 (키워드)
    @Schema(description = "태그 목록", example = "[\"아웃도어\",\"태양광\"]")
    @JsonProperty("tags")
    private List<String> tags;

    // 한글 설명: 사람이 읽기 좋은 카테고리 이름 (예: "전자제품 > 노트북")
    @Schema(description = "카테고리 이름(사람 친화)", example = "아웃도어 > 백팩")
    @JsonProperty("category_name")
    private String categoryName;

    // 한글 설명: 백엔드에서 매핑하기 좋은 코드/슬러그 (예: "ELECTRONICS_LAPTOP")
    @Schema(description = "카테고리 코드/슬러그", example = "OUTDOOR_BAG")
    @JsonProperty("category_code")
    private String categoryCode;
}
