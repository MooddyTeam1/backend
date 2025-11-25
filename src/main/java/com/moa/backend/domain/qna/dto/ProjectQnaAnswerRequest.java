// 한글 설명: 프로젝트 Q&A 답변 등록/수정 요청 DTO
package com.moa.backend.domain.qna.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "프로젝트 Q&A 답변 등록/수정 요청")
public class ProjectQnaAnswerRequest {

    // 한글 설명: 메이커가 작성하는 답변 내용
    @Schema(description = "답변 내용", example = "안녕하세요, 1차 배송은 3월 15일부터 순차적으로 진행됩니다 :)")
    private String answer;
}
