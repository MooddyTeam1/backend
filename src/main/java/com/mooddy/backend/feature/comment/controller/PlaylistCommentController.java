package com.mooddy.backend.feature.comment.controller;

import com.mooddy.backend.feature.comment.dto.PlaylistCommentRequestDto;
import com.mooddy.backend.feature.comment.dto.PlaylistCommentResponseDto;
import com.mooddy.backend.feature.comment.dto.PlaylistCommentUpdateRequestDto;
import com.mooddy.backend.feature.comment.service.PlaylistCommentService;
import com.mooddy.backend.feature.user.domain.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/playlists/{playlistId}/comments")
@RequiredArgsConstructor
public class PlaylistCommentController {

    private final PlaylistCommentService playlistCommentService;

    @GetMapping
    public ResponseEntity<Page<PlaylistCommentResponseDto>> getComments(
            @PathVariable Long playlistId,
            @AuthenticationPrincipal User user,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<PlaylistCommentResponseDto> comments = playlistCommentService.getComments(playlistId, pageable, user);
        return ResponseEntity.ok(comments);
    }

    @PostMapping
    public ResponseEntity<PlaylistCommentResponseDto> createComment(
            @PathVariable Long playlistId,
            @AuthenticationPrincipal User user,
            @RequestBody @Valid PlaylistCommentRequestDto request
    ) {
        PlaylistCommentResponseDto response = playlistCommentService.createComment(playlistId, request, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<PlaylistCommentResponseDto> updateComment(
            @PathVariable Long playlistId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal User user,
            @RequestBody @Valid PlaylistCommentUpdateRequestDto request
    ) {
        PlaylistCommentResponseDto response = playlistCommentService.updateComment(playlistId, commentId, request, user);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long playlistId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal User user
    ) {
        playlistCommentService.deleteComment(playlistId, commentId, user);
        return ResponseEntity.noContent().build();
    }
}
