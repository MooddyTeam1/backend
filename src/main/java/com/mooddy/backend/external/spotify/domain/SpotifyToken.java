package com.mooddy.backend.external.spotify.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.mooddy.backend.feature.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "spotifytoken")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SpotifyToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String accessToken;

    private Long expiresIn;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    @JsonManagedReference
    private User user;

    @PrePersist
    public void oncreate() {
        this.createdAt = LocalDateTime.now();
    }

    public boolean isExpired() {
        if (expiresIn == null) {
            return true;
        }
        return createdAt.plusSeconds(expiresIn).isBefore(LocalDateTime.now());
    }
}
