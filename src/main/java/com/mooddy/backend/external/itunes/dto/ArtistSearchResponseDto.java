package com.mooddy.backend.external.itunes.dto;

public record ArtistSearchResponseDto(
        String artistId,
        String artistName,
        String primaryGenreName
) {
}
