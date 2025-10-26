package com.mooddy.backend.feature.track.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "tracks", indexes = {
        @Index(name = "idx_track_title", columnList = "title"),
        @Index(name = "idx_track_artist", columnList = "artist")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Track {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private Long trackId;

    private String spotifyId;

    @Column(nullable = false)
    private String title;

    private String artist;

    private String album;

    private Integer durationMs;

    private String albumCoverUrl;

    private String releaseDate;

    private LocalDateTime cachedAt;

    private String previewUrl;

    private String primaryGenreName;

    @PrePersist
    protected void onCreate() {
        cachedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        cachedAt = LocalDateTime.now();
    }
}
