package com.moa.backend.domain.maker.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * 한글 설명: 프로젝트 공지(소식) 생성/수정 요청 DTO.
 * - 제목, 내용, 공개 여부, 알림 여부만 전달한다.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "프로젝트 공지 생성/수정 요청")
public class ProjectNoticeCreateRequest {

    @Schema(description = "공지 제목", example = "1차 배송 일정 안내")
    private String title;

    @Schema(description = "공지 내용(Markdown)", example = "1차 배송은 3월 15일부터 순차적으로 시작됩니다.")
    private String content;

    @Schema(description = "공개 여부 (true: 서포터에게 공개)", example = "true")
    private Boolean isPublic;

    @Schema(description = "공지 등록 시 서포터 알림 발송 여부", example = "true")
    private Boolean notifySupporters;
}
