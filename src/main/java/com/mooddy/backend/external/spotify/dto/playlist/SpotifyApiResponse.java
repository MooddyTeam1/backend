package com.mooddy.backend.external.spotify.dto.playlist;

import lombok.Data;

import java.util.List;

// Spotify API 전체
@Data
public class SpotifyApiResponse {
    private List<SpotifyResponse> items;
}
