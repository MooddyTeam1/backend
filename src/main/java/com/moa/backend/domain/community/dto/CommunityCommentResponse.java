package com.moa.backend.domain.community.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import com.moa.backend.domain.community.entity.ProjectCommunityComment;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "커뮤니티 댓글 응답")
public class CommunityCommentResponse {

    @Schema(description = "댓글 ID", example = "4001")
    private Long commentId;
    @Schema(description = "작성자 ID", example = "1")
    private Long userId;
    @Schema(description = "작성자 이름", example = "홍길동")
    private String userName;
    @Schema(description = "내용", example = "응원합니다!")
    private String content;
    @Schema(description = "작성 시각", example = "2025-01-05T12:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "좋아요 수", example = "3")
    private long likeCount;
    @Schema(description = "내가 좋아요 눌렀는지", example = "false")
    private boolean liked;

    // 부모 댓글 ID (일반 댓글이면 null)
    @Schema(description = "부모 댓글 ID(대댓글일 때)", example = "4000")
    private Long parentCommentId;

    // 이 댓글의 대댓글 목록
    @Schema(description = "대댓글 목록")
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
