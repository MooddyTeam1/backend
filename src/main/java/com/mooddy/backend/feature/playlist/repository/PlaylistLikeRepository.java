package com.mooddy.backend.feature.playlist.repository;

import com.mooddy.backend.feature.playlist.domain.Playlist;
import com.mooddy.backend.feature.playlist.domain.PlaylistLike;
import com.mooddy.backend.feature.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaylistLikeRepository extends JpaRepository<PlaylistLike, Long> {

    // 특정유저가 특정 플레이리스트 좋아요 했는지 확인
    boolean existsByUserAndPlaylist(User user, Playlist playlist);

    // 좋아요 갯수 조회
    Long countByPlaylistId(Long playlistId);

    // 특정유저가 특정 플레이리스트에 누른 좋아요 삭제
    void deleteByUserAndPlaylist(User user, Playlist playlist);
}
