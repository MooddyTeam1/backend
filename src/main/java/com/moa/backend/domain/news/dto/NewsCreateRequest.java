package com.moa.backend.domain.news.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "프로젝트 소식 생성 요청")
public class NewsCreateRequest {
    @Schema(description = "제목", example = "배송 일정 공지")
    private String title;
    @Schema(description = "내용", example = "1차 배송을 시작합니다.")
    private String content;
    @Schema(description = "이미지 URL 목록", example = "[\"https://cdn.moa.com/news1.png\"]")
    private List<String> imageUrls;   // S3 URL 리스트
}
