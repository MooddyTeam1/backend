package com.mooddy.backend.feature.user.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mooddy.backend.external.spotify.domain.SpotifyToken;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String nickname;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column
    private LocalDate birthDate;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_At")
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    private AuthProvider provider;

    private boolean enabled;

    private boolean onboardingCompleted = false;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonBackReference
    private SpotifyToken spotifyToken;

    @ElementCollection
    @CollectionTable(
            name = "user_genre",
            joinColumns = @JoinColumn(name = "user_id")
    )
    @Column(name = "genre")
    private Set<String> favoriteGenres = new HashSet<>();

    @ElementCollection
    @CollectionTable(
            name = "user_artist",
            joinColumns = @JoinColumn(name = "user_id")
    )
    @Column(name = "artist")
    private Set<String> favoriteArtists = new HashSet<>();

    // 내가 팔로우하는 사람들
    @OneToMany(mappedBy = "follower", fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnore
    private Set<Follow> followingList = new HashSet<>();

    // 나를 팔로우하는 사람들
    @OneToMany(mappedBy = "following", fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnore
    private Set<Follow> followerList = new HashSet<>();

    @PrePersist
    public void prePersist() { enabled = true; }

    // UserDetails interface
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public boolean isEnabled() { return enabled; }

    @Override
    public String getUsername() { return email; }
}
