package com.mooddy.backend.feature.user.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String username;
    private String bio;
    private String location;

    @ElementCollection
    private List<String> favoriteGenres;

    @ElementCollection
    private List<String> favoriteArtists;

    private String musicStyle;

    private String spotifyLink;
    private String youtubeMusicLink;
    private String appleMusicLink;

    private String profileImageUrl;
}