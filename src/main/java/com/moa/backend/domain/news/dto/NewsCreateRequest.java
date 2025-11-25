package com.moa.backend.domain.news.dto;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewsCreateRequest {
    private String title;
    private String content;
    private List<String> imageUrls;   // S3 URL 리스트
}
