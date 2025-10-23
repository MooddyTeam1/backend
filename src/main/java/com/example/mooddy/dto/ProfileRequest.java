package com.example.mooddy.dto;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class ProfileRequest {
    private String email;
    private String username;
    private String bio;
    private String location;
    private List<String> favoriteGenres;
    private List<String> favoriteArtists;
    private String musicStyle;
    private String SpotifyLink;
    private String youtubeMusicLink;
    private String appleMusicLink;
}

