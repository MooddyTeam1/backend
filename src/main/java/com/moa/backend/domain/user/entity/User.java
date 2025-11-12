package com.moa.backend.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "users")
@ToString(exclude = "socialConnections")
@EqualsAndHashCode(exclude = "socialConnections")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_id_seq")
    @SequenceGenerator(name = "user_id_seq", sequenceName = "user_id_seq", allocationSize = 1)
    private Long id;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "password", length = 255)
    private String password;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private UserRole role;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @Column(name = "image_url")
    private String imageUrl;

    // ✅ enum 이름 그대로 문자열로 저장 (LOCAL / GOOGLE / KAKAO)
    @Enumerated(EnumType.STRING)
    @Column(name = "provider", nullable = false)
    private AuthProvider provider;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<SocialConnection> socialConnections = new HashSet<>();

    // 기존에는 onUpdate 메서드 내부에 선언되어 라이프사이클 메서드와 겹치던 socialConnections 필드를
    // 클래스 레벨로 이동시켜 JPA 매핑과 컬렉션 초기화가 정상 동작하도록 수정했습니다.

    // 🔥 provider 추가된 생성자들
    private User(String email, String password, String name, UserRole role, AuthProvider provider) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.role = role;
        this.provider = provider;
    }

    private User(String email, String name, UserRole role, AuthProvider provider) {
        this.email = email;
        this.name = name;
        this.role = role;
        this.provider = provider;
    }

    // 🔥 일반 회원가입: 항상 LOCAL
    public static User createUser(String email, String encodedPassword, String name) {
        return new User(email, encodedPassword, name, UserRole.USER, AuthProvider.LOCAL);
    }

    // 🔥 소셜 회원가입: 어떤 provider인지 외부에서 넘겨주도록 변경
    public static User createSocialUser(String email, String name, String imageUrl, AuthProvider provider) {
        User user = new User(email, name, UserRole.USER, provider);
        user.setImageUrl(imageUrl);
        return user;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // 소셜 연결 추가
    public void addSocialConnection(String provider, String providerId, String providerEmail) {
        SocialConnection connection = new SocialConnection();
        connection.setUser(this);
        connection.setProvider(provider);
        connection.setProviderId(providerId);
        connection.setProviderEmail(providerEmail);
        connection.setConnectedAt(LocalDateTime.now());

        socialConnections.add(connection);
    }

    // 소셜 연결 제거
    public void removeSocialConnection(String provider) {
        socialConnections.removeIf(conn -> conn.getProvider().equals(provider));
    }

    // 특정 제공자 연결 여부 확인
    public boolean hasProvider(String provider) {
        return socialConnections.stream()
                .anyMatch(conn -> conn.getProvider().equals(provider));
    }

}
