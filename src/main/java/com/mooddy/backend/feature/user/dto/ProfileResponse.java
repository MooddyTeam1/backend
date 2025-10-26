package com.mooddy.backend.feature.user.dto;

import com.mooddy.backend.feature.user.domain.Profile;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class ProfileResponse {
    private String email;
    private String username;
    private String bio;
    private String location;
    private List<String> favoriteGenres;
    private List<String> favoriteArtists;
    private String musicStyle;
    private String spotifyLink;
    private String youtubeMusicLink;
    private String appleMusicLink;

    public static ProfileResponse fromEntity(Profile profile) {
        return ProfileResponse.builder()
                .email(profile.getEmail())
                .username(profile.getUsername())
                .bio(profile.getBio())
                .location(profile.getLocation())
                .favoriteGenres(profile.getFavoriteGenres())
                .favoriteArtists(profile.getFavoriteArtists())
                .musicStyle(profile.getMusicStyle())
                .spotifyLink(profile.getSpotifyLink())
                .youtubeMusicLink(profile.getYoutubeMusicLink())
                .appleMusicLink(profile.getAppleMusicLink())
                .build();
    }
}
