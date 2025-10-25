package com.mooddy.backend.global.config;

import com.mooddy.backend.feature.user.domain.Artist;
import com.mooddy.backend.feature.user.domain.Genre;
import com.mooddy.backend.feature.user.repository.ArtistRepository;
import com.mooddy.backend.feature.user.repository.GenreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final GenreRepository genreRepository;
    private final ArtistRepository artistRepository;

    @Override
    public void run(String... args) throws Exception {
        if (genreRepository.count() == 0) {
            genreRepository.saveAll(List.of(
                    new Genre(null, "발라드"),
                    new Genre(null, "힙합"),
                    new Genre(null, "댄스"),
                    new Genre(null, "록"),
                    new Genre(null, "R&B"),
                    new Genre(null, "POP"),
                    new Genre(null,"트로트"),
                    new Genre(null,"클래식")
            ));
        }

        if (artistRepository.count() == 0) {
            artistRepository.saveAll(List.of(
                    new Artist(null, "아이유"),
                    new Artist(null,"GD"),
                    new Artist(null, "블랙핑크"),
                    new Artist(null,"빅뱅"),
                    new Artist(null,"세븐틴"),
                    new Artist(null,"에스파"),
                    new Artist(null,"방탄소년단"),
                    new Artist(null,"콜드플레이"),
                    new Artist(null,"테일러 스위프트"),
                    new Artist(null, "에드 시런")
            ));
        }
    }
}
