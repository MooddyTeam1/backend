package com.mooddy.backend.feature.track.repository;

import com.mooddy.backend.feature.track.domain.Track;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TrackRepository extends JpaRepository<Track, Long> {
    Optional<Track> findByTrackId(Long trackId);
}
