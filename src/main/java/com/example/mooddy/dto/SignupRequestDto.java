package com.example.mooddy.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class SignupRequestDto {
    // 인증/기본 정보 (AuthService에서 사용)
    @NotBlank (message = "닉네임을 입력해주세요.")
    @Size(min = 3, max = 20, message = "닉네임은 3 ~ 20글자여야 합니다.")
    private String nickname;

    @NotBlank (message = "이메일을 입력해주세요.")
    @Email(message = "올바른 이메일 형식이여야 합니다.")
    private String email;

    @NotBlank (message = "비밀번호를 입력해주세요.")
    @Size(min = 6, message = "비밀번호는 6자 이상이여야 합니다. ")
    private String password;

    @NotNull (message = "생년월일을 입력해주세요.")
    private LocalDate birthDate;

    // 프로필 정보 (UserService에서 사용)
    private String username;
    private String bio;
    private String location;
    private List<String> favoriteGenres;
    private List<String> favoriteArtists;
    private String musicStyle;
    private String spotifyLink;
    private String youtubeMusicLink;
    private String appleMusicLink;
}