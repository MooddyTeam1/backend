package com.moa.backend.domain.admin.dto;

import com.moa.backend.domain.user.entity.User;
import com.moa.backend.domain.user.entity.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "관리자용 사용자 응답")
public class UserResponse {
    @Schema(description = "사용자 ID", example = "1003")
    private Long id;
    @Schema(description = "이메일", example = "supporter1@moa.com")
    private String email;
    @Schema(description = "이름", example = "홍길동")
    private String name;
    @Schema(description = "권한 (USER, MAKER, ADMIN)", example = "MAKER")
    private UserRole role;

    public static UserResponse from(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole())
                .build();
    }
}
