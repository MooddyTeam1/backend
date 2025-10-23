package com.example.mooddy.dto;

import com.example.mooddy.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailResponseDto {
    private Long id;
    private String nickname;
    private String email;
    private LocalDate birthDate;
    private User.AuthProvider provider;
    private boolean enabled;
    private boolean onboardingCompleted;

    // 프로필 필드 추가
    private String username;
    private String bio;
    private String location;
    private List<String> favoriteGenres;
    private List<String> favoriteArtists;
    private String musicStyle;
    private String profileImageUrl;
    private String spotifyLink;
    private String youtubeMusicLink;
    private String appleMusicLink;


    // 단일 User 엔티티만 받도록 수정
    public static UserDetailResponseDto fromEntity(User user) {
        return UserDetailResponseDto.builder()
                .id(user.getId())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .birthDate(user.getBirthDate())
                .provider(user.getProvider())
                .enabled(user.isEnabled())
                .onboardingCompleted(user.isOnboardingCompleted())
                .username(user.getUsername())
                .bio(user.getBio())
                .location(user.getLocation())
                .favoriteGenres(user.getFavoriteGenres())
                .favoriteArtists(user.getFavoriteArtists())
                .musicStyle(user.getMusicStyle())
                .profileImageUrl(user.getProfileImageUrl())
                .spotifyLink(user.getSpotifyLink())
                .youtubeMusicLink(user.getYoutubeMusicLink())
                .appleMusicLink(user.getAppleMusicLink())
                .build();
    }
}