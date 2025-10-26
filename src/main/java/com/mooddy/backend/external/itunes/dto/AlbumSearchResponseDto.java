package com.mooddy.backend.external.itunes.dto;

public record AlbumSearchResponseDto(
        String collectionId,
        String collectionName,
        String artistName,
        String artworkUrl100,
        Integer trackCount,
        String releaseDate,
        String primaryGenreName
) {
}
