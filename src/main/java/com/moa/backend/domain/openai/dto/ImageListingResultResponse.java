// src/main/java/com/moa/backend/domain/ai/dto/ImageListingResultResponse.java
package com.moa.backend.domain.openai.dto;

import com.moa.backend.domain.project.entity.Category;
import lombok.Builder;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

// 한글 설명: 프론트로 내려줄 최종 응답 DTO (enum Category 사용)
@Builder
@Schema(description = "AI 이미지 기반 상품 설명 응답")
public record ImageListingResultResponse(
        @Schema(description = "생성된 제목", example = "휴대용 솔라 백팩")
        String title,
        @Schema(description = "짧은 요약", example = "태양광 충전과 LTE 트래커를 내장한 백팩")
        String shortDescription,
        @Schema(description = "스토리(마크다운 가능)", example = "### 왜 만들었나\n여행자와 하이커를 위해...")
        String story,
        @Schema(description = "태그 목록", example = "[\"아웃도어\",\"태양광\"]")
        List<String> tags,
        @Schema(description = "카테고리 enum", example = "FASHION")
        Category category,
        @Schema(description = "카테고리 한글명", example = "패션")
        String categoryName // 한글 이름도 같이 내려주고 싶으면 유지
) {
}
