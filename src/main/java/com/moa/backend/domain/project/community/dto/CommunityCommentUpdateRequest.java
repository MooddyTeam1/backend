package com.moa.backend.domain.project.community.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "커뮤니티 댓글 수정 요청")
public class CommunityCommentUpdateRequest {
    @Schema(description = "수정할 내용", example = "배송 일정을 알려주세요.")
    private String content;
}
