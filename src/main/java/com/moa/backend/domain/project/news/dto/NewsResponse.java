package com.moa.backend.domain.project.news.dto;

import com.moa.backend.domain.project.news.entity.ProjectNews;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "프로젝트 소식 응답")
public class NewsResponse {
    @Schema(description = "소식 ID", example = "2001")
    private Long newsId;
    @Schema(description = "프로젝트 ID", example = "101")
    private Long projectId;
    @Schema(description = "제목", example = "배송 일정 공지")
    private String title;
    @Schema(description = "내용", example = "1차 배송을 시작합니다.")
    private String content;
    @Schema(description = "상단 고정 여부", example = "false")
    private boolean pinned;
    @Schema(description = "생성 시각", example = "2025-01-05T12:00:00")
    private LocalDateTime createdAt;
    @Schema(description = "이미지 URL 목록", example = "[\"https://cdn.moa.com/news1.png\"]")
    private List<String> images;

    public static NewsResponse from(ProjectNews news) {
        return NewsResponse.builder()
                .newsId(news.getId())
                .projectId(news.getProject().getId())
                .title(news.getTitle())
                .content(news.getContent())
                .pinned(news.isPinned())
                .createdAt(news.getCreatedAt())
                .images(news.getImages().stream()
                        .map(img -> img.getImageUrl())
                        .toList())
                .build();
    }
}
