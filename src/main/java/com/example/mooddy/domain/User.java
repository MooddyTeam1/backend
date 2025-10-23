package com.example.mooddy.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {

    // User 엔티티에 Profile 필드 통합
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String nickname;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column
    private LocalDate birthDate;

    // --- Profile Fields Start ---
    private String username;
    private String bio;
    private String location;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> favoriteGenres;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> favoriteArtists;

    private String musicStyle;
    private String spotifyLink;
    private String youtubeMusicLink;
    private String appleMusicLink;
    private String profileImageUrl;
    // --- Profile Fields End ---

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_At")
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    private AuthProvider provider;

    @Builder.Default
    private boolean enabled = true;

    @Builder.Default
    private boolean onboardingCompleted = false;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getUsername() {
        return this.email; // 혹은 nickname 등 인증에 사용할 필드
    }

    public enum AuthProvider {
        LOCAL, GOOGLE, SPOTIFY
    }
}