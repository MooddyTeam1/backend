package com.mooddy.backend.feature.track.repository;

import com.mooddy.backend.feature.track.domain.Track;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TrackRepository extends JpaRepository<Track, Long> {
    Optional<Track> findByTrackId(Long trackId);

    // 공백제거 및 소문자 변환
    @Query("SELECT t FROM Track t " +
            "WHERE LOWER(FUNCTION('REPLACE', t.title, ' ', '')) = LOWER(FUNCTION('REPLACE', :title, ' ', '')) " +
            "AND LOWER(FUNCTION('REPLACE', t.artist, ' ', '')) = LOWER(FUNCTION('REPLACE', :artist, ' ', '')) ")
    Optional<Track> findByTitleAndArtistAndAlbum(String title, String artist);
}
