package com.moa.backend.domain.makernews.dto;

import com.moa.backend.domain.makernews.entity.MakerNews;
import com.moa.backend.domain.makernews.entity.MakerNewsType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 한글 설명: 메이커 소식 응답 DTO.
 * - 명세서의 MakerNewsResponseDTO에 대응.
 */
@Getter
@Builder
public class MakerNewsResponse {

    // 한글 설명: 소식 고유 ID
    private Long id;

    // 한글 설명: 메이커 ID
    private Long makerId;

    // 한글 설명: 소식 제목
    private String title;

    // 한글 설명: 마크다운 형식의 소식 내용
    private String contentMarkdown;

    // 한글 설명: 소식 유형 (EVENT | NOTICE | NEW_PRODUCT)
    private MakerNewsType newsType;

    // 한글 설명: 생성일시
    private LocalDateTime createdAt;

    // 한글 설명: 수정일시
    private LocalDateTime updatedAt;

    /**
     * 한글 설명: 엔티티 → 응답 DTO 변환 편의 메서드.
     */
    public static MakerNewsResponse from(MakerNews news) {
        return MakerNewsResponse.builder()
                .id(news.getId())
                .makerId(news.getMaker().getId())
                .title(news.getTitle())
                .contentMarkdown(news.getContentMarkdown())
                .newsType(news.getNewsType())
                .createdAt(news.getCreatedAt())
                .updatedAt(news.getUpdatedAt())
                .build();
    }
}
