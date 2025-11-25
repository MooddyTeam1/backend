package com.moa.backend.domain.maker.dto.manageproject;

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
public class ProjectQnaResponse {

    // 한글 설명: Q&A ID
    private Long id;

    // 한글 설명: 질문자 이름/닉네임 (서포터 닉네임 우선)
    private String questionerName;

    // 한글 설명: 질문자 ID (userId)
    private Long questionerId;

    // 한글 설명: 질문 내용
    private String question;

    // 한글 설명: 답변 내용 (없을 수 있음)
    private String answer;

    // 한글 설명: 상태 (PENDING, ANSWERED)
    private String status;

    // 한글 설명: 질문일시
    private LocalDateTime createdAt;

    // 한글 설명: 답변일시 (없을 수 있음)
    private LocalDateTime answeredAt;
}
