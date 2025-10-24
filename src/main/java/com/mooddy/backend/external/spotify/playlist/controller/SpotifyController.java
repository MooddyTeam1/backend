package com.mooddy.backend.external.spotify.playlist.controller;

import com.mooddy.backend.external.spotify.playlist.service.SpotifyService;
import com.mooddy.backend.feature.playlist.domain.Playlist;
import com.mooddy.backend.feature.playlist.dto.PlaylistResponseDto;
import com.mooddy.backend.feature.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/spotify/playlists")
@RequiredArgsConstructor
public class SpotifyController {

    private final SpotifyService spotifyService;

    @GetMapping("/me")
    public ResponseEntity<List<PlaylistResponseDto>> getSpotifyPlaylists(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestHeader String spotifyAccessToken
    ) {
        Long userId;
        if (userDetails instanceof User user) {
            userId = user.getId();
        } else {
            return ResponseEntity.badRequest().build();
        }

        List<Playlist> playlists = spotifyService.getSpotifyPlaylists(spotifyAccessToken, userId);

        List<PlaylistResponseDto> responseDto = playlists.stream()
                .map(playlist -> PlaylistResponseDto.from(playlist, user))
                .toList();

        return ResponseEntity.ok(responseDto);
    }
}
