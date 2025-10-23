package com.mooddy.backend.feature.playlist.dto;

import com.mooddy.backend.feature.playlist.domain.Playlist;
import com.mooddy.backend.feature.playlist.domain.PlaylistTrack;
import com.mooddy.backend.feature.playlist.domain.Visibility;
import com.mooddy.backend.feature.user.domain.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public record PlaylistResponseDto(
        Long id,
        String title,
        String description,
        String coverImageUrl,
        Visibility visibility,
        Long userId,
        String userNickname,
        List<PlaylistTrackResponseDto> tracks,
        List<Long> sharedUserIds,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static PlaylistResponseDto from(Playlist playlist, User requester) {
        boolean isOwner = requester != null
                && playlist.getUser().getId().equals(requester.getId());

        List<Long> sharedUserIds = isOwner
                ? playlist.getPlaylistVisibilities().stream()
                .map(pv -> pv.getUser().getId())
                .collect(Collectors.toList())
                : Collections.emptyList();  // 소유자가 아니면 목록 숨김

        return new PlaylistResponseDto(
                playlist.getId(),
                playlist.getTitle(),
                playlist.getDescription(),
                playlist.getCoverImageUrl(),
                playlist.getVisibility(),
                playlist.getUser().getId(),
                playlist.getUser().getNickname(),
                playlist.getPlaylistTracks().stream()
                        .sorted(Comparator.comparingInt(PlaylistTrack::getPosition))
                        .map(PlaylistTrackResponseDto::from)
                        .collect(Collectors.toList()),
                sharedUserIds,
                playlist.getCreatedAt(),
                playlist.getUpdatedAt()
        );
    }
}

