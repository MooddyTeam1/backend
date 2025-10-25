package com.mooddy.backend.feature.playlist.repository;

import com.mooddy.backend.feature.playlist.domain.Playlist;
import com.mooddy.backend.feature.playlist.domain.Visibility;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlaylistRepository extends JpaRepository<Playlist, Long> {
    List<Playlist> findByUserId(Long userId);

    List<Playlist> findByVisibility(Visibility visibility);

    /**
     * 플레이리스트를 tracks와 함께 조회 (Fetch Join)
     * Lazy Loading 문제 해결을 위해 한 번에 모든 데이터를 가져옴
     */
    @Query("SELECT p FROM Playlist p " +
            "LEFT JOIN FETCH p.playlistTracks pt " +
            "LEFT JOIN FETCH pt.track " +
            "WHERE p.id = :id")
    Optional<Playlist> findByIdWithTracks(@Param("id") Long id);

    /**
     * 플레이리스트 제목 또는 설명으로 검색 (부분 검색)
     * PUBLIC 플레이리스트만 검색
     */
    @Query("SELECT p FROM Playlist p " +
            "WHERE (LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND p.visibility = 'PUBLIC'")
    Page<Playlist> searchByTitleOrDescription(@Param("keyword") String keyword, Pageable pageable);

    /**
     * 노래 제목 또는 아티스트명으로 플레이리스트 검색 (부분 검색)
     * PUBLIC 플레이리스트만 검색
     */
    @Query("SELECT DISTINCT p FROM Playlist p " +
            "JOIN p.playlistTracks pt " +
            "JOIN pt.track t " +
            "WHERE (LOWER(t.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(t.artist) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND p.visibility = 'PUBLIC'")
    Page<Playlist> searchByTrack(@Param("keyword") String keyword, Pageable pageable);
}
