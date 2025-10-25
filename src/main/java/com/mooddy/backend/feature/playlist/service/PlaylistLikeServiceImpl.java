package com.mooddy.backend.feature.playlist.service;

import com.mooddy.backend.feature.playlist.domain.Playlist;
import com.mooddy.backend.feature.playlist.domain.PlaylistLike;
import com.mooddy.backend.feature.playlist.repository.PlaylistLikeRepository;
import com.mooddy.backend.feature.playlist.repository.PlaylistRepository;
import com.mooddy.backend.feature.user.domain.User;
import com.mooddy.backend.feature.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlaylistLikeServiceImpl implements PlaylistLikeService {

    private final PlaylistLikeRepository playlistLikeRepository;
    private final PlaylistRepository playlistRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    // 좋아요 상태 변경
    public boolean toggleLike(Long playlistId, Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다"));

        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new IllegalArgumentException("플레이리스트를 찾을 수 없습니다"));

        boolean alreadyLiked = playlistLikeRepository.existsByUserAndPlaylist(user, playlist);

        if (alreadyLiked) {
            playlistLikeRepository.deleteByUserAndPlaylist(user, playlist);
            return false;
        } else {
            PlaylistLike playlistLike = PlaylistLike.builder()
                    .user(user)
                    .playlist(playlist)
                    .build();

            playlistLikeRepository.save(playlistLike);
            return true;
        }
    }

    @Override
    public Long getLikeCount(Long playlistId) {
        return playlistLikeRepository.countByPlaylistId(playlistId);
    }

    // 조회용 (프론트에서 버튼 상태 표시용)
    @Override
    public boolean isPlaylistLikedByUser(Long playlistId, Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다"));

        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new IllegalArgumentException("플레이리스트를 찾을 수 없습니다"));

        return playlistLikeRepository.existsByUserAndPlaylist(user, playlist);
    }
}
