package com.mooddy.backend.external.spotify.repository;

import com.mooddy.backend.external.spotify.domain.SpotifyToken;
import com.mooddy.backend.feature.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpotifyTokenRepository extends JpaRepository<SpotifyToken, Long> {
    Optional<SpotifyToken> findByUser(User user);

    Optional<SpotifyToken> findByAccessToken(String SpotifyToken);

}
