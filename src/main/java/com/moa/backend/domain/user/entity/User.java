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
@ToString(exclude = "SocialConnections")
@EqualsAndHashCode(exclude = "SocialConnections")
public class User {

    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    //임시 Mock 데이터 때문에 충돌날수 있어서 10번부터 만들어지게 함
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_id_seq")
    @jakarta.persistence.SequenceGenerator(name = "user_id_seq", sequenceName = "user_id_seq", initialValue = 10, allocationSize = 1)
    private Long id;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "password", nullable = false, length = 255)
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

    private User(String email, String password, String name, UserRole role) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.role = role;
    }

    public static User createUser(String email, String encodedPassword, String name) {
        return new User(email, encodedPassword, name, UserRole.USER);
    }


    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
        @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
        private Set<SocialConnection> socialConnections = new HashSet<>();

        // 소셜 연결 추가
        public void addSocialConnection (String provider, String providerId, String providerEmail){
            SocialConnection connection = new SocialConnection();
            connection.setUser(this);
            connection.setProvider(provider);
            connection.setProviderId(providerId);
            connection.setProviderEmail(providerEmail);
            connection.setConnectedAt(LocalDateTime.now());

            socialConnections.add(connection);
        }

        // 소셜 연결 제거
        public void removeSocialConnection (String provider){
            socialConnections.removeIf(conn -> conn.getProvider().equals(provider));
        }

        // 특정 제공자 연결 여부 확인
        public boolean hasProvider (String provider){
            return socialConnections.stream()
                    .anyMatch(conn -> conn.getProvider().equals(provider));
        }
    }
}