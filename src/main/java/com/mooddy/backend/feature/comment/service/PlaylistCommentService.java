package com.mooddy.backend.feature.comment.service;

import com.mooddy.backend.feature.comment.dto.PlaylistCommentRequestDto;
import com.mooddy.backend.feature.comment.dto.PlaylistCommentResponseDto;
import com.mooddy.backend.feature.comment.dto.PlaylistCommentUpdateRequestDto;
import com.mooddy.backend.feature.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PlaylistCommentService {

    Page<PlaylistCommentResponseDto> getComments(Long playlistId, Pageable pageable, User requester);

    PlaylistCommentResponseDto createComment(Long playlistId, PlaylistCommentRequestDto request, User requester);

    PlaylistCommentResponseDto updateComment(Long playlistId, Long commentId,
                                             PlaylistCommentUpdateRequestDto request, User requester);

    void deleteComment(Long playlistId, Long commentId, User requester);
}

