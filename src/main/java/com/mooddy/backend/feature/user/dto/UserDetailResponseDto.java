package com.mooddy.backend.feature.user.dto;

import com.mooddy.backend.feature.user.domain.AuthProvider;
import com.mooddy.backend.feature.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
// 화면 표시용
public class UserDetailResponseDto {
    private Long id;
    private String nickname;
    private String email;
    private LocalDate birthDate;
    private AuthProvider provider;
    private boolean enabled;
    private boolean onboardingCompleted;

    public static UserDetailResponseDto fromEntity(User user) {
        return UserDetailResponseDto.builder()
                .id(user.getId())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .birthDate(user.getBirthDate())
                .provider(user.getProvider())
                .enabled(user.isEnabled())
                .onboardingCompleted(user.isOnboardingCompleted())
                .build();
    }
}
