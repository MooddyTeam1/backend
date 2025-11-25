package com.moa.backend.domain.qna.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * 한글 설명: 서포터가 Q&A 질문을 남길 때 사용하는 요청 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "프로젝트 Q&A 질문 생성 요청")
public class ProjectQnaCreateRequest {

    @Schema(description = "질문 내용", example = "배송 일정이 언제인가요?")
    private String question;

    @Schema(description = "비공개 여부 (true이면 나와 메이커만 볼 수 있음)", example = "false")
    private Boolean isPrivate;
}
