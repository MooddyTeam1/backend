package com.mooddy.backend.feature.user.service;

import com.mooddy.backend.feature.user.domain.Artist;
import com.mooddy.backend.feature.user.domain.Genre;
import com.mooddy.backend.feature.user.domain.User;
import com.mooddy.backend.feature.user.dto.OnboardingRequest;
import com.mooddy.backend.feature.user.repository.ArtistRepository;
import com.mooddy.backend.feature.user.repository.GenreRepository;
import com.mooddy.backend.feature.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserOnboardingServiceImpl implements UserOnboardingService{

    private final UserRepository userRepository;
    private final GenreRepository genreRepository;
    private final ArtistRepository artistRepository;

    @Override
    public void completedOnboarding(Long userId, OnboardingRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수없습니다"));

        Set<Genre> genres = new HashSet<>(genreRepository.findAllById(request.getGenreIds()));
        Set<Artist> artists = new HashSet<>(artistRepository.findAllById(request.getArtistIds()));

        user.setFavoriteGenres(genres);
        user.setFavoriteArtists(artists);
        user.setOnboardingCompleted(true);

        userRepository.save(user);
    }
}
