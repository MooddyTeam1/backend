package com.moa.backend.domain.user.entity;

import com.moa.backend.domain.onboarding.model.OnboardingStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * 한글 설명: 서비스의 기본 사용자 엔티티
 *  - 이메일/비밀번호/이름/역할(Role)/소셜 Provider 정보
 *  - 온보딩 진행 상태(onboardingStatus)
 *  - 소셜 로그인 연결 정보(SocialConnection) 1:N
 */
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

    // 한글 설명: 로그인 provider (LOCAL / GOOGLE / KAKAO 등)
    @Enumerated(EnumType.STRING)
    @Column(name = "provider", nullable = false)
    private AuthProvider provider;

    // 한글 설명: 온보딩 진행 상태 (NOT_STARTED / SKIPPED / COMPLETED)
    @Enumerated(EnumType.STRING)
    @Column(name = "onboarding_status", nullable = false, length = 20)
    private OnboardingStatus onboardingStatus;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<SocialConnection> socialConnections = new HashSet<>();

    // =====================================================================
    // 생성자 & 정적 팩토리 메서드
    // =====================================================================

    // 한글 설명: 내부용 생성자 - 비밀번호 있는 경우
    private User(String email, String password, String name, UserRole role, AuthProvider provider) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.role = role;
        this.provider = provider;
        // 온보딩은 기본적으로 시작 안 한 상태
        this.onboardingStatus = OnboardingStatus.NOT_STARTED;
    }

    // 한글 설명: 내부용 생성자 - 소셜 로그인 등 비밀번호 없는 경우
    private User(String email, String name, UserRole role, AuthProvider provider) {
        this.email = email;
        this.name = name;
        this.role = role;
        this.provider = provider;
        this.onboardingStatus = OnboardingStatus.NOT_STARTED;
    }

    /**
     * 한글 설명: 일반 회원가입용 생성 메서드
     *  - provider는 항상 LOCAL
     */
    public static User createUser(String email, String encodedPassword, String name) {
        return new User(email, encodedPassword, name, UserRole.USER, AuthProvider.LOCAL);
    }

    /**
     * 한글 설명: 소셜 회원가입용 생성 메서드
     *  - provider(KAKAO/GOOGLE 등)는 호출하는 쪽에서 전달
     */
    public static User createSocialUser(String email, String name, String imageUrl, AuthProvider provider) {
        User user = new User(email, name, UserRole.USER, provider);
        user.setImageUrl(imageUrl);
        return user;
    }

    // =====================================================================
    // JPA 라이프사이클 콜백
    // =====================================================================

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;

        // 한글 설명: 혹시 null이면 기본값을 한 번 더 방어적으로 설정
        if (this.onboardingStatus == null) {
            this.onboardingStatus = OnboardingStatus.NOT_STARTED;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // =====================================================================
    // 온보딩 / 소셜 연결 관련 편의 메서드
    // =====================================================================

    /**
     * 한글 설명: 온보딩 상태 변경 (NOT_STARTED → SKIPPED/COMPLETED 등)
     */
    public void updateOnboardingStatus(OnboardingStatus status) {
        this.onboardingStatus = status;
    }

    /**
     * 한글 설명: 소셜 연결 추가
     */
    public void addSocialConnection(String provider, String providerId, String providerEmail) {
        SocialConnection connection = new SocialConnection();
        connection.setUser(this);
        connection.setProvider(provider);
        connection.setProviderId(providerId);
        connection.setProviderEmail(providerEmail);
        connection.setConnectedAt(LocalDateTime.now());

        socialConnections.add(connection);
    }

    /**
     * 한글 설명: 특정 provider의 소셜 연결 제거
     */
    public void removeSocialConnection(String provider) {
        socialConnections.removeIf(conn -> conn.getProvider().equals(provider));
    }

    /**
     * 한글 설명: 특정 provider로 이미 연결되어 있는지 여부 체크
     */
    public boolean hasProvider(String provider) {
        return socialConnections.stream()
                .anyMatch(conn -> conn.getProvider().equals(provider));
    }

}
