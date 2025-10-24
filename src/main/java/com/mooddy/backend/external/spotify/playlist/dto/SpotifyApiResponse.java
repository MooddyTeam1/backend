package com.mooddy.backend.external.spotify.playlist.dto;

import lombok.Data;

import java.util.List;

// Spotify API 전체
@Data
public class SpotifyApiResponse {
    private List<SpotifyResponse> items;
}
