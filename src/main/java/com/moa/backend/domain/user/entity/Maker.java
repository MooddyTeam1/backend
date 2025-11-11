package com.moa.backend.domain.user.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Table(name = "makers")
@Builder
public class Maker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_user_id", nullable = false, unique = true)
    private User owner;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "business_number")
    private String businessNumber;

    @Column(name = "business_name")
    private String businessName;

    @Column(name = "established_at")
    private LocalDate establishedAt;

    @Column(name = "industry_type")
    private String industryType;

    @Column(name = "representative")
    private String representative;

    @Column(name = "location")
    private String location;

    @Column(name = "product_intro", columnDefinition = "TEXT")
    private String productIntro;

    @Column(name = "core_competencies", columnDefinition = "TEXT")
    private String coreCompetencies;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "contact_email")
    private String contactEmail;

    @Column(name = "contact_phone")
    private String contactPhone;

    @Column(name = "tech_stack_json", columnDefinition = "TEXT")
    private String techStackJson;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    private Maker(User owner, String name) {
        this.owner = owner;
        this.name = name;
        this.contactEmail = owner.getEmail();
    }

    public static Maker createDefault(User owner) {
        return new Maker(owner, owner.getEmail());
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
    public void updateName(String name) {
        this.name = name;
    }

    public void updateBusinessNumber(String businessNumber) {
        this.businessNumber = businessNumber;
    }

    public void updateBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public void updateEstablishedAt(LocalDate establishedAt) {
        this.establishedAt = establishedAt;
    }

    public void updateIndustryType(String industryType) {
        this.industryType = industryType;
    }

    public void updateRepresentative(String representative) {
        this.representative = representative;
    }

    public void updateLocation(String location) {
        this.location = location;
    }

    public void updateProductIntro(String productIntro) {
        this.productIntro = productIntro;
    }

    public void updateCoreCompetencies(String coreCompetencies) {
        this.coreCompetencies = coreCompetencies;
    }

    public void updateImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void updateContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public void updateContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public void updateTechStackJson(String techStackJson) {
        this.techStackJson = techStackJson;
    }
}