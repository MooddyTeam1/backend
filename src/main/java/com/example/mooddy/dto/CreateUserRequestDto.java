package com.example.mooddy.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class CreateUserRequestDto {
    // SignupRequestDto와 동일한 필드를 포함 (모든 핵심/프로필 필드 포함)
    private String nickname;
    private String email;
    private String password;
    private LocalDate birthDate;

    private String username;
    private String bio;
    private String location;
    private List<String> favoriteGenres;
    private List<String> favoriteArtists;
    private String musicStyle;
    private String SpotifyLink;
    private String youtubeMusicLink;
    private String appleMusicLink;

    public static CreateUserRequestDto fromSignupRequest(SignupRequestDto request) {
        // SignupRequestDto에서 변환하는 팩토리 메서드
        return CreateUserRequestDto.builder()
                .nickname(request.getNickname())
                .email(request.getEmail())
                .password(request.getPassword())
                .birthDate(request.getBirthDate())
                .username(request.getUsername())
                .bio(request.getBio())
                .location(request.getLocation())
                .favoriteGenres(request.getFavoriteGenres())
                .favoriteArtists(request.getFavoriteArtists())
                .musicStyle(request.getMusicStyle())
                .SpotifyLink(request.getSpotifyLink())
                .youtubeMusicLink(request.getYoutubeMusicLink())
                .appleMusicLink(request.getAppleMusicLink())
                .build();
    }
}