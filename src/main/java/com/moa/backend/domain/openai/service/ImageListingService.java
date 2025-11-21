// src/main/java/com/moa/backend/domain/ai/service/ImageListingService.java
package com.moa.backend.domain.openai.service;

import com.moa.backend.domain.openai.client.OpenAiListingClient;
import com.moa.backend.domain.openai.dto.ImageListingResultResponse;
import com.moa.backend.domain.openai.dto.OpenAiListingResponse;
import com.moa.backend.domain.project.entity.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ImageListingService {

    private final OpenAiListingClient openAiListingClient;

    /**
     * 한글 설명:
     *  - 이미지 파일과 힌트/톤을 받아 OpenAI에서 설명/카테고리를 생성하고,
     *  - Category enum으로 매핑해서 반환한다.
     */
    @Transactional
    public ImageListingResultResponse generateListingFromImage(MultipartFile image,
                                                               String hint,
                                                               String tone) {
        try {
            byte[] bytes = image.getBytes();

            // 1) OpenAI 호출 (원시 응답)
            OpenAiListingResponse ai = openAiListingClient.generateFromImage(bytes, hint, tone);

            // 2) category_code → Category enum 매핑
            Category category = mapToCategory(ai.getCategoryCode());

            // 3) 최종 응답 DTO 빌드
            return ImageListingResultResponse.builder()
                    .title(ai.getTitle())
                    .shortDescription(ai.getShortDescription())
                    .story(ai.getStory())
                    .tags(ai.getTags())
                    .category(category)
                    .categoryName(ai.getCategoryName())
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("이미지 처리 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * 한글 설명:
     *  - OpenAI가 반환한 category_code 문자열을
     *    프로젝트의 Category enum으로 매핑하는 유틸 메서드.
     *  - 잘못된 값이 오면 기본값으로 TECH 를 사용한다.
     */
    private Category mapToCategory(String categoryCode) {
        if (categoryCode == null || categoryCode.isBlank()) {
            return Category.TECH; // 기본값 (원하면 다른 것으로 바꿔도 됨)
        }

        String normalized = categoryCode.trim().toUpperCase();

        try {
            // 한글 설명: enum 이름과 정확히 일치하면 바로 매핑
            return Category.valueOf(normalized);
        } catch (IllegalArgumentException ex) {
            // 한글 설명: 혹시 "TECHNOLOGY" 등으로 나올 수 있으니 간단한 보정 로직 추가
            return switch (normalized) {
                case "TECHNOLOGY" -> Category.TECH;
                case "DESIGNER", "DESIGNS" -> Category.DESIGN;
                case "FOODS" -> Category.FOOD;
                case "CLOTHES", "APPAREL", "FASHION_ITEM" -> Category.FASHION;
                case "COSMETICS", "MAKEUP" -> Category.BEAUTY;
                case "HOME", "HOME_LIVING", "LIVING" -> Category.HOME_LIVING;
                case "GAMES" -> Category.GAME;
                case "ARTS", "CRAFT", "CRAFTS" -> Category.ART;
                case "PUBLISHING", "BOOK", "BOOKS" -> Category.PUBLISH;
                default -> Category.TECH; // 완전 이상한 값이면 일단 TECH로 fallback
            };
        }
    }
}
