package com.mooddy.backend.feature.user.dto;

import com.mooddy.backend.feature.user.domain.User;


public record UserSearchResponseDto(
        Long id,
        String nickname,
        String profileImageUrl
) {
    public static UserSearchResponseDto fromEntity(User user) {
        return new UserSearchResponseDto(
                user.getId(),
                user.getNickname(),
                user.getProfileImageUrl()
        );
    }
}
