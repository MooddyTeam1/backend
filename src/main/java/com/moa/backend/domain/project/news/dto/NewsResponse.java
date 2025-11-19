package com.moa.backend.domain.project.news.dto;

import com.moa.backend.domain.project.news.entity.ProjectNews;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewsResponse {
    private Long newsId;
    private Long projectId;
    private String title;
    private String content;
    private boolean pinned;
    private LocalDateTime createdAt;
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
