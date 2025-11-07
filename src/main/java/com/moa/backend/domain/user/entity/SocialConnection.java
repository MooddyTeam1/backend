package com.moa.backend.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.Id;

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

    private String provider; // google, github, kakao
    private String providerId; // 제공자에서의 사용자 ID
    private String providerEmail; // 제공자에서의 이메일

    private LocalDateTime connectedAt;
    private LocalDateTime lastUsedAt;
}