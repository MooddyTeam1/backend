package com.mooddy.backend.feature.comment.dto;

import com.mooddy.backend.feature.comment.domain.PlaylistComment;

import java.time.LocalDateTime;
import java.util.List;

public record PlaylistCommentResponseDto(
        Long id,
        Long userId,
        String userNickname,
        String userProfileImageUrl,
        String content,
        Long parentId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<PlaylistCommentResponseDto> replies
) {
    public static PlaylistCommentResponseDto from(PlaylistComment comment, List<PlaylistCommentResponseDto> replies) {
        return new PlaylistCommentResponseDto(
                comment.getId(),
                comment.getUser().getId(),
                comment.getUser().getNickname(),
                comment.getUser().getProfileImageUrl(),
                comment.getContent(),
                comment.getParent() != null ? comment.getParent().getId() : null,
                comment.getCreatedAt(),
                comment.getUpdatedAt(),
                replies
        );
    }
}

