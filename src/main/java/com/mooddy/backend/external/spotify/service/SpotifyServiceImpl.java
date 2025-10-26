package com.mooddy.backend.external.spotify.service;

import com.mooddy.backend.external.spotify.dto.playlist.SpotifyApiResponse;
import com.mooddy.backend.external.spotify.dto.track.SpotifyApiTrackResponse;
import com.mooddy.backend.external.spotify.dto.track.SpotifyTrackResponse;
import com.mooddy.backend.feature.playlist.domain.Playlist;
import com.mooddy.backend.feature.playlist.domain.PlaylistTrack;
import com.mooddy.backend.feature.playlist.repository.PlaylistRepository;
import com.mooddy.backend.feature.playlist.repository.PlaylistTrackRepository;
import com.mooddy.backend.feature.track.domain.Track;
import com.mooddy.backend.feature.track.repository.TrackRepository;
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
    private final TrackRepository trackRepository;
    private final PlaylistTrackRepository playlistTrackRepository;


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

    @Override
    public void SpotifyPlaylistTracks(String spotifyAccessToken, Playlist playlist) {
        // Spotify API를 호출하여 해당 플레이리스트의 트랙 정보를 가져옴
        SpotifyApiTrackResponse response = webClientBuilder.build()
                .get()
                .uri("https://api.spotify.com/v1/playlists/{id}/tracks", playlist.getSpotifyPlaylistId())
                .headers(headers -> headers.setBearerAuth(spotifyAccessToken))
                .retrieve()
                .bodyToMono(SpotifyApiTrackResponse.class)
                .block();

        if (response == null || response.getItems() == null) return;

        int position = 0;
        for (SpotifyApiTrackResponse.Item item : response.getItems()) {
            // 각 트랙 정보를 엔티티로 변환
            SpotifyTrackResponse trackResponse = item.getTrack();
            Track track = trackResponse.ToEntity();

            // 트랙 정보를 DB에 저장
            trackRepository.save(track);

            // 플레이리스트와 트랙의 관계 생성 후 DB에 저장
            PlaylistTrack spotifyTrack = PlaylistTrack.builder()
                    .playlist(playlist)
                    .track(track)
                    .position(position++)   
                    .build();
            playlistTrackRepository.save(spotifyTrack);

            playlist.getPlaylistTracks().add(spotifyTrack);
        }
    }
}
