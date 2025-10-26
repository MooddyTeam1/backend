package com.mooddy.backend.external.spotify.dto.track;

import lombok.Data;

import java.util.List;

@Data
public class SpotifyApiTrackResponse {
    private List<Item> items;

    @Data
    public static class Item {
        private SpotifyTrackResponse track;
    }
}
