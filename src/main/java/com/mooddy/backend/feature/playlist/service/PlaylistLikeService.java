package com.mooddy.backend.feature.playlist.service;

public interface PlaylistLikeService {

    // 특정 유저가 특정 플레이리스트에 좋아요 토글
    boolean toggleLike(Long playlistId, Long userId);

    Long getLikeCount(Long playlistId);

    // 특정 유저가 특정플레이리스트 좋아요 눌렀는지 조회
    boolean isPlaylistLikedByUser(Long playlistId, Long userId);

}
