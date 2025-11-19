package com.moa.backend.domain.project.community.dto;

import com.moa.backend.domain.project.community.entity.ProjectCommunityComment;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommunityCommentResponse {

    private Long commentId;
    private Long userId;
    private String userName;
    private String content;
    private LocalDateTime createdAt;

    public static CommunityCommentResponse from(ProjectCommunityComment c) {
        return CommunityCommentResponse.builder()
                .commentId(c.getId())
                .userId(c.getUser().getId())
                .userName(c.getUser().getName())
                .content(c.getContent())
                .createdAt(c.getCreatedAt())
                .build();
    }
}
