package com.mooddy.backend.feature.user.service;

import com.mooddy.backend.feature.user.dto.UserSearchResponseDto;

import java.util.List;


public interface UserService {
    List<UserSearchResponseDto> searchUsersByNickname(String nickname, Long currentUserId);
}
