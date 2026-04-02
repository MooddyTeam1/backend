package com.moa.backend.domain.maker.dto.manageproject;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 한글 설명: 프로젝트 Q&A DTO.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "ProjectQnaResponse DTO")
public class ProjectQnaResponse {

    // 한글 설명: Q&A ID
    @Schema(description = "id", example = "1")
    private Long id;

    // 한글 설명: 질문자 이름/닉네임 (서포터 닉네임 우선)
    @Schema(description = "questionerName", example = "questionerName")
    private String questionerName;

    // 한글 설명: 질문자 ID (userId)
    @Schema(description = "questionerId", example = "1")
    private Long questionerId;

    // 한글 설명: 질문 내용
    @Schema(description = "question", example = "question")
    private String question;

    // 한글 설명: 답변 내용 (없을 수 있음)
    @Schema(description = "answer", example = "answer")
    private String answer;

    // 한글 설명: 상태 (PENDING, ANSWERED)
    @Schema(description = "status", example = "status")
    private String status;

    // 한글 설명: 질문일시
    @Schema(description = "createdAt", example = "2025-11-01T10:00:00")
    private LocalDateTime createdAt;

    // 한글 설명: 답변일시 (없을 수 있음)
    @Schema(description = "answeredAt", example = "2025-11-01T10:00:00")
    private LocalDateTime answeredAt;
}
