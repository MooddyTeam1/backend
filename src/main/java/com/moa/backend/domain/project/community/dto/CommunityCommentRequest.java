package com.moa.backend.domain.project.community.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "커뮤니티 댓글 생성 요청")
public class CommunityCommentRequest {

    @Schema(description = "댓글 내용", example = "응원합니다!")
    private String content;

    // 대댓글일 경우, 부모 댓글 ID (일반 댓글이면 null)
    @Schema(description = "부모 댓글 ID(대댓글일 때)", example = "10")
    private Long parentCommentId;
}
