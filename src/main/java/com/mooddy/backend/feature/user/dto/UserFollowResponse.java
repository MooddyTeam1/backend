package com.mooddy.backend.feature.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserFollowResponse {
    Long id;
    String nickname;
    private String profileImageUrl;
}
