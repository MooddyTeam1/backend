package com.mooddy.backend.feature.playlist.controller;

import com.mooddy.backend.feature.playlist.dto.PlaylistLikeResponse;
import com.mooddy.backend.feature.playlist.dto.PlaylistResponseDto;
import com.mooddy.backend.feature.playlist.service.PlaylistLikeService;
import com.mooddy.backend.feature.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/playlists")
@RequiredArgsConstructor
public class PlaylistLikeController {

    private final PlaylistLikeService playlistLikeService;

    // 좋아요 토글
    @PostMapping("/{playlistsId}/like")
    public ResponseEntity<PlaylistLikeResponse> togglelike(
            @PathVariable Long playlistsId,
            @AuthenticationPrincipal User user
    ) {
        boolean toggleLike = playlistLikeService.toggleLike(playlistsId, user.getId());
        Long likeCount = playlistLikeService.getLikeCount(playlistsId);

        PlaylistLikeResponse response = PlaylistLikeResponse.builder()
                .playlistId(playlistsId)
                .liked(toggleLike)
                .likeCount(likeCount)
                .build();

        return ResponseEntity.ok(response);
    }

    // 좋아요 상태 조회 (프론트 버튼 용)
    @GetMapping("/{playlistId}/like/status")
    public ResponseEntity<PlaylistLikeResponse> getLikeStatus(
            @PathVariable Long playlistId,
            @AuthenticationPrincipal User user
    ) {
        boolean likeStaus = playlistLikeService.isPlaylistLikedByUser(playlistId, user.getId());
        Long likeCount = playlistLikeService.getLikeCount(playlistId);

        PlaylistLikeResponse response = PlaylistLikeResponse.builder()
                .playlistId(playlistId)
                .liked(likeStaus)
                .likeCount(likeCount)
                .build();

        return ResponseEntity.ok(response);
    }
}
