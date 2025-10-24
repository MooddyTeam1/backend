package com.mooddy.backend.external.itunes.dto;

import com.mooddy.backend.feature.track.dto.TrackSearchResponseDto;

import java.util.List;

public record ItunesSearchResultDto(
        List<TrackSearchResponseDto> tracks,
        List<AlbumSearchResponseDto> albums,
        List<ArtistSearchResponseDto> artists
) {
}
