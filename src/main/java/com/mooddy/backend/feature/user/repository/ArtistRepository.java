package com.mooddy.backend.feature.user.repository;

import com.mooddy.backend.feature.user.domain.Artist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ArtistRepository extends JpaRepository<Artist, Long> {

    Optional<Artist> findByName(String name);
}
