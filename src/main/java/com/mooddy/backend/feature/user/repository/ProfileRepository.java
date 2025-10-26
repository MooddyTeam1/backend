package com.mooddy.backend.feature.user.repository;

import com.mooddy.backend.feature.user.domain.Profile;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ProfileRepository extends CrudRepository<Profile, Long> {
    Optional<Profile> findByEmail(String email);
}
