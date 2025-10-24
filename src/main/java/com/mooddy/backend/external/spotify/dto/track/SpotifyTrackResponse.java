package com.mooddy.backend.external.spotify.dto.track;

import com.mooddy.backend.feature.track.domain.Track;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class SpotifyTrackResponse {
    private String id; // Spotify track ID
    private String name; // track 제목
    private List<Artist> artists;
    private Album album;
    private Integer duration_ms;
    private String preview_url;

    @Data
    public static class Artist {
        private String name;
    }

    @Data
    public static class Album {
        private String name;
        private List<Image> images;
        private String release_date;
    }

    @Data
    public static class Image {
        private String url;
    }

    // 로컬 트랙 엔티티로 변환
    public Track ToEntity() {
        String artistNames = artists.stream().map(Artist::getName).collect(Collectors.joining(", "));

        return Track.builder()
                .spotifyId(id)
                .title(name)
                .artist(artistNames)
                .album(album.getName())
                .durationMs(duration_ms)
                .releaseDate(album.getRelease_date())
                .previewUrl(preview_url)
                .build();
    }
}
