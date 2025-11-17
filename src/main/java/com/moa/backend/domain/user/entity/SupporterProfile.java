package com.moa.backend.domain.user.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "supporter_profiles")
public class SupporterProfile {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "display_name")
    private String displayName;

    @Column(name = "bio", columnDefinition = "TEXT")
    private String bio;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "phone")
    private String phone;

    @Column(name = "postal_code")
    private String postalCode;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "address1")
    private String address1;

    @Column(name = "address2")
    private String address2;

    @Column(name = "interests", columnDefinition = "TEXT")
    private String interests; // ["React", "Node.js"] 같은 JSON 문자열

    private SupporterProfile(User user) {
        this.user = user;
        //this.userId = user.getId();
    }

    public static SupporterProfile createEmpty(User user) {
        return new SupporterProfile(user);
    }

    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void updateDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void updateImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void updateBio(String bio) {
        this.bio = bio;
    }

    public void updatePhone(String phone) {
        this.phone = phone;
    }

    public void updatePostalCode(String postalCode) {
        this.postalCode = postalCode;
    }
    public void updateAddress1(String address1) { this.address1 = address1; }
    public void updateAddress2(String address2) { this.address2 = address2; }
    public void updateInterests(String interests) { this.interests = interests; }
}