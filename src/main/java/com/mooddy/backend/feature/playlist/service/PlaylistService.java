package com.mooddy.backend.feature.playlist.service;

import com.mooddy.backend.feature.playlist.dto.PlaylistForkRequestDto;
import com.mooddy.backend.feature.playlist.dto.PlaylistRequestDto;
import com.mooddy.backend.feature.playlist.dto.PlaylistResponseDto;
import com.mooddy.backend.feature.playlist.dto.SearchType;
import com.mooddy.backend.feature.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PlaylistService {
    PlaylistResponseDto createPlaylist(User user, PlaylistRequestDto request);

    List<PlaylistResponseDto> getUserPlaylists(Long userId, User requester);

    List<PlaylistResponseDto> getPublicPlaylists();

    PlaylistResponseDto getPlaylist(Long playlistId, User user);

    PlaylistResponseDto updatePlaylist(Long playlistId, User user, PlaylistRequestDto request);

    void deletePlaylist(Long playlistId, User user);

    PlaylistResponseDto addTrackToPlaylist(Long playlistId, User user, Long trackId);

    void removeTrackFromPlaylist(Long playlistId, User user, Long trackId);

    PlaylistResponseDto updateTrackPosition(Long playlistId, User user, Long trackId, Integer newPosition);

    PlaylistResponseDto forkPlaylist(Long playlistId, User user, PlaylistForkRequestDto request);

    Page<PlaylistResponseDto> searchPlaylists(String keyword, SearchType type, Pageable pageable);
}