package com.mooddy.backend.external.spotify.playlist.service;

import com.mooddy.backend.external.spotify.playlist.dto.SpotifyApiResponse;
import com.mooddy.backend.feature.playlist.domain.Playlist;
import com.mooddy.backend.feature.playlist.repository.PlaylistRepository;
import com.mooddy.backend.feature.user.domain.User;
import com.mooddy.backend.feature.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SpotifyServiceImpl implements SpotifyService{

    private final WebClient.Builder webClientBuilder;
    private final PlaylistRepository playlistRepository;
    private final UserRepository userRepository;


    @Override
    public List<Playlist> getSpotifyPlaylists(String spotifyAccessToken, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // Spotify API 호출
        SpotifyApiResponse response = webClientBuilder.build()
                .get()
                .uri("https://api.spotify.com/v1/me/playlists")
                .headers(headers -> headers.setBearerAuth(spotifyAccessToken))
                .retrieve()
                .bodyToMono(SpotifyApiResponse.class)// SpotifyApiResponse로 매핑
                .block();

        if (response == null || response.getItems() == null) {
            return List.of();
        }

        // DTO → Entity 변환 (SpotifyResponse → Playlist, User 포함)
       List<Playlist> playlists = response.getItems().stream()
                .map(spotifyResponse -> spotifyResponse.toEntity(user))
                .collect(Collectors.toList());

        return playlistRepository.saveAll(playlists);
    }
}
