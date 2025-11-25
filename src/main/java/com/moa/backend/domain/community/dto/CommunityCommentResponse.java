package com.moa.backend.domain.community.dto;

import com.moa.backend.domain.community.entity.ProjectCommunityComment;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

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

    private long likeCount;
    private boolean liked;

    // 부모 댓글 ID (일반 댓글이면 null)
    private Long parentCommentId;

    // 이 댓글의 대댓글 목록
    private List<CommunityCommentResponse> replies;

    public static CommunityCommentResponse from(
            ProjectCommunityComment c,
            long likeCount,
            boolean liked
    ) {
        return CommunityCommentResponse.builder()
                .commentId(c.getId())
                .userId(c.getUser().getId())
                .userName(c.getUser().getName())
                .content(c.getContent())
                .createdAt(c.getCreatedAt())
                .likeCount(likeCount)
                .liked(liked)
                .parentCommentId(
                        c.getParent() != null ? c.getParent().getId() : null
                )
                // replies 는 서비스에서 따로 세팅
                .build();
    }
}
