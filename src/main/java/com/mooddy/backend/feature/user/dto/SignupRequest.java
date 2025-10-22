package com.mooddy.backend.feature.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class SignupRequest {
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
}
