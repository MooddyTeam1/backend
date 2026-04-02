package com.moa.backend.domain.maker.dto.manageproject;

import io.swagger.v3.oas.annotations.media.Schema;
import com.moa.backend.domain.maker.entity.ProjectNews;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 한글 설명: 프로젝트 공지 DTO.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "ProjectNoticeResponse DTO")
public class ProjectNoticeResponse {

    // 한글 설명: 공지 ID
    @Schema(description = "id", example = "1")
    private Long id;

    // 한글 설명: 공지 제목
    @Schema(description = "title", example = "title")
    private String title;

    // 한글 설명: 공지 내용 (Markdown)
    @Schema(description = "content", example = "content")
    private String content;

    // 한글 설명: 공개 여부 (true: 서포터에게 공개)
    @Schema(description = "isPublic", example = "true")
    private Boolean isPublic;

    // 한글 설명: 공지 등록 시 서포터 알림 발송 여부
    @Schema(description = "notifySupporters", example = "true")
    private Boolean notifySupporters;

    // 한글 설명: 공지 생성일시
    @Schema(description = "createdAt", example = "2025-11-01T10:00:00")
    private LocalDateTime createdAt;

    // 한글 설명: 공지 수정일시
    @Schema(description = "updatedAt", example = "2025-11-01T10:00:00")
    private LocalDateTime updatedAt;

    // 한글 설명: ProjectNews 엔티티 → DTO 변환 헬퍼
    public static ProjectNoticeResponse from(ProjectNews news) {
        return ProjectNoticeResponse.builder()
                .id(news.getId())
                .title(news.getTitle())
                .content(news.getContent())
                .isPublic(news.getIsPublic())
                .notifySupporters(news.getNotifySupporters())
                .createdAt(news.getCreatedAt())
                .updatedAt(news.getUpdatedAt())
                .build();
    }
}
