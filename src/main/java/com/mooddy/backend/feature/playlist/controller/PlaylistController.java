package com.mooddy.backend.feature.playlist.controller;

import com.mooddy.backend.feature.playlist.dto.AddTrackRequestDto;
import com.mooddy.backend.feature.playlist.dto.PlaylistRequestDto;
import com.mooddy.backend.feature.playlist.dto.PlaylistResponseDto;
import com.mooddy.backend.feature.playlist.service.PlaylistService;
import com.mooddy.backend.feature.user.domain.User;
import lombok.RequiredArgsConstructor;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 플레이리스트 관리 API
 */
@RestController
@RequestMapping("/api/playlists")
@RequiredArgsConstructor
public class PlaylistController {

    private final PlaylistService playlistService;

    /**
     * 플레이리스트 생성
     */
    @PostMapping
    public ResponseEntity<PlaylistResponseDto> createPlaylist(
            @AuthenticationPrincipal User user,
            @RequestBody PlaylistRequestDto request) {
        PlaylistResponseDto playlist = playlistService.createPlaylist(user, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(playlist);
    }

    /**
     * 특정 사용자의 플레이리스트 목록 조회
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PlaylistResponseDto>> getUserPlaylists(
            @PathVariable Long userId,
            @AuthenticationPrincipal User requester
    ) {
        List<PlaylistResponseDto> playlists = playlistService.getUserPlaylists(userId, requester);
        return ResponseEntity.ok(playlists);
    }

    /**
     * 공개 플레이리스트 목록 조회
     */
    @GetMapping("/public")
    public ResponseEntity<List<PlaylistResponseDto>> getPublicPlaylists() {
        List<PlaylistResponseDto> playlists = playlistService.getPublicPlaylists();
        return ResponseEntity.ok(playlists);
    }

    /**
     * 특정 플레이리스트 조회
     */
    @GetMapping("/{playlistId}")
    public ResponseEntity<PlaylistResponseDto> getPlaylist(
            @PathVariable Long playlistId,
            @AuthenticationPrincipal User user) {
        PlaylistResponseDto playlist = playlistService.getPlaylist(playlistId, user);
        return ResponseEntity.ok(playlist);
    }

    /**
     * 플레이리스트 정보 수정
     */
    @PutMapping("/{playlistId}")
    public ResponseEntity<PlaylistResponseDto> updatePlaylist(
            @PathVariable Long playlistId,
            @AuthenticationPrincipal User user,
            @RequestBody PlaylistRequestDto request) {
        PlaylistResponseDto playlist = playlistService.updatePlaylist(playlistId, user, request);
        return ResponseEntity.ok(playlist);
    }

    /**
     * 플레이리스트 삭제
     */
    @DeleteMapping("/{playlistId}")
    public ResponseEntity<Void> deletePlaylist(
            @PathVariable Long playlistId,
            @AuthenticationPrincipal User user) {
        playlistService.deletePlaylist(playlistId, user);
        return ResponseEntity.noContent().build();
    }

    /**
     * 플레이리스트에 곡 추가
     */
    @PostMapping("/{playlistId}/tracks")
    public ResponseEntity<PlaylistResponseDto> addTrackToPlaylist(
            @PathVariable Long playlistId,
            @AuthenticationPrincipal User user,
            @RequestBody AddTrackRequestDto request) {
        PlaylistResponseDto playlist = playlistService.addTrackToPlaylist(playlistId, user, request.trackId());
        return ResponseEntity.ok(playlist);
    }

    /**
     * 플레이리스트에서 곡 제거
     */
    @DeleteMapping("/{playlistId}/tracks/{trackId}")
    public ResponseEntity<Void> removeTrackFromPlaylist(
            @PathVariable Long playlistId,
            @PathVariable Long trackId,
            @AuthenticationPrincipal User user) {
        playlistService.removeTrackFromPlaylist(playlistId, user, trackId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 플레이리스트 내 곡 순서 변경
     * http://localhost:8080/api/playlists/1/tracks/1/position?newPosition=1
     */
    @PutMapping("/{playlistId}/tracks/{trackId}/position")
    public ResponseEntity<PlaylistResponseDto> updateTrackPosition(
            @PathVariable Long playlistId,
            @PathVariable Long trackId,
            @AuthenticationPrincipal User user,
            @RequestParam Integer newPosition) {
        PlaylistResponseDto playlist = playlistService.updateTrackPosition(playlistId, user, trackId, newPosition);
        return ResponseEntity.ok(playlist);
    }
}
