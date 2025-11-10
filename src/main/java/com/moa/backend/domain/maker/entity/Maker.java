package com.moa.backend.domain.maker.entity;

import com.moa.backend.domain.user.entity.User;
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
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "makers")
public class Maker {

    @Id
    //Mock 데이터와 충돌을 방지하기 위해 임시로 10번부터 생성
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "maker_id_seq")
    @SequenceGenerator(name = "maker_id_seq", sequenceName = "maker_id_seq", initialValue = 10, allocationSize = 1)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_user_id", nullable = false, unique = true)
    private User owner;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "business_name", nullable = false, length = 100)
    private String businessName;

    @Column(name = "business_number", length = 50)
    private String businessNumber;

    @Column(name = "representative", length = 50)
    private String representative;

    @Column(name = "established_at")
    private LocalDate establishedAt;

    @Column(name = "industry_type", length = 100)
    private String industryType;

    @Column(name = "location", length = 255)
    private String location;

    @Column(name = "product_intro", columnDefinition = "TEXT")
    private String productIntro;

    @Column(name = "core_competencies", columnDefinition = "TEXT")
    private String coreCompetencies;

    @Column(name = "image_url", length = 255)
    private String imageUrl;

    @Column(name = "contact_email", length = 100)
    private String contactEmail;

    @Column(name = "contact_phone", length = 30)
    private String contactPhone;

    @Column(name = "tech_stack_json", columnDefinition = "TEXT")
    private String techStackJson;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    private Maker(
            User owner,
            String name,
            String businessName,
            String businessNumber,
            String representative,
            LocalDate establishedAt,
            String industryType,
            String location,
            String productIntro,
            String coreCompetencies,
            String imageUrl,
            String contactEmail,
            String contactPhone,
            String techStackJson
    ) {
        this.owner = owner;
        this.name = name;
        this.businessName = businessName;
        this.businessNumber = businessNumber;
        this.representative = representative;
        this.establishedAt = establishedAt;
        this.industryType = industryType;
        this.location = location;
        this.productIntro = productIntro;
        this.coreCompetencies = coreCompetencies;
        this.imageUrl = imageUrl;
        this.contactEmail = contactEmail;
        this.contactPhone = contactPhone;
        this.techStackJson = techStackJson;
    }

    public static Maker create(User owner, String businessName) {
        String baseName = businessName != null ? businessName : owner.getEmail();
        return Maker.builder()
                .owner(owner)
                .name(baseName)
                .businessName(baseName)
                .contactEmail(owner.getEmail())
                .build();
    }

    public void updateBusinessInfo(
            String name,
            String businessName,
            String businessNumber,
            String representative,
            LocalDate establishedAt,
            String industryType,
            String location,
            String productIntro,
            String coreCompetencies,
            String imageUrl,
            String contactEmail,
            String contactPhone,
            String techStackJson
    ) {
        this.name = name;
        this.businessName = businessName;
        this.businessNumber = businessNumber;
        this.representative = representative;
        this.establishedAt = establishedAt;
        this.industryType = industryType;
        this.location = location;
        this.productIntro = productIntro;
        this.coreCompetencies = coreCompetencies;
        this.imageUrl = imageUrl;
        this.contactEmail = contactEmail;
        this.contactPhone = contactPhone;
        this.techStackJson = techStackJson;
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
}
