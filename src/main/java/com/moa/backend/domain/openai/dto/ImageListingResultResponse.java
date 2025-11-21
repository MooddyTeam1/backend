// src/main/java/com/moa/backend/domain/ai/dto/ImageListingResultResponse.java
package com.moa.backend.domain.openai.dto;

import com.moa.backend.domain.project.entity.Category;
import lombok.Builder;

import java.util.List;

// 한글 설명: 프론트로 내려줄 최종 응답 DTO (enum Category 사용)
@Builder
public record ImageListingResultResponse(
        String title,
        String shortDescription,
        String story,
        List<String> tags,
        Category category,
        String categoryName // 한글 이름도 같이 내려주고 싶으면 유지
) {
}
