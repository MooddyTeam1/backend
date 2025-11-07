package com.moa.backend.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;

// org.springframework.data.annotation.Id는 jakarta.persistence.Id와 용도가 겹쳐
// JPA 매핑에 충돌이 발생하여 제거했습니다.

import java.time.LocalDateTime;

@Entity
@Table(name = "social_connections")
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = "user")
@EqualsAndHashCode(exclude = "user")
public class SocialConnection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String provider;       // google, github, kakao 등
    private String providerId;     // 제공자에서의 사용자 ID
    private String providerEmail;  // 제공자에서의 이메일

    private LocalDateTime connectedAt;
    private LocalDateTime lastUsedAt;
}
