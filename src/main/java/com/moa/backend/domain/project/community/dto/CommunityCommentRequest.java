package com.moa.backend.domain.project.community.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommunityCommentRequest {

    private String content;

    // 대댓글일 경우, 부모 댓글 ID (일반 댓글이면 null)
    private Long parentCommentId;
}
