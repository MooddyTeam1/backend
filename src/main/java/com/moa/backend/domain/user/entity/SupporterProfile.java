package com.moa.backend.domain.user.entity;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moa.backend.domain.onboarding.model.AcquisitionChannel;
import com.moa.backend.domain.onboarding.model.BudgetRange;
import com.moa.backend.domain.onboarding.model.FundingExperience;
import com.moa.backend.domain.onboarding.model.NotificationPreference;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 한글 설명: 서포터(후원자) 프로필 엔티티
 *  - 기본 프로필 정보 (닉네임, 한 줄 소개, 이미지, 전화번호 등)
 *  - 주소 정보
 *  - 관심 카테고리(온보딩 Step1) : interests 컬럼에 JSON 문자열로 저장
 *  - 온보딩 Step2 정보: 예산, 경험, 유입 경로, 알림 설정 등
 */
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

    /**
     * 한글 설명: 온보딩 Step1 - 관심 카테고리 목록
     *  - 예: ["TECH","DESIGN","FOOD"] 같은 JSON 문자열
     */
    @Column(name = "interests", columnDefinition = "TEXT")
    private String interests;

    /**
     * 한글 설명: 온보딩 Step1 (선택) - 선호 프로젝트 스타일 목록
     *  - 예: ["실용템 위주","유니크한 굿즈"] 같은 JSON 문자열
     */
    @Column(name = "preferred_styles", columnDefinition = "TEXT")
    private String preferredStyles;

    /**
     * 한글 설명: 온보딩 Step2 - 평소 후원 예산 범위 (선택)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "budget_range", length = 30)
    private BudgetRange budgetRange;

    /**
     * 한글 설명: 온보딩 Step2 - 크라우드펀딩 후원 경험 (선택)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "funding_experience", length = 20)
    private FundingExperience fundingExperience;

    /**
     * 한글 설명: 온보딩 Step2 - MOA 유입 경로 (선택)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "acquisition_channel", length = 30)
    private AcquisitionChannel acquisitionChannel;

    /**
     * 한글 설명: 온보딩 Step2 - 유입 경로가 기타(OTHER)일 때 상세 입력
     */
    @Column(name = "acquisition_channel_etc", length = 100)
    private String acquisitionChannelEtc;

    /**
     * 한글 설명: 온보딩 Step2 - 알림 수신 설정 (선택)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "notification_preference", length = 20)
    private NotificationPreference notificationPreference;

    // =====================================================================
    // 생성자 & 정적 팩토리
    // =====================================================================

    private SupporterProfile(User user) {
        this.user = user;
    }

    /**
     * 한글 설명: 유저 생성 시 비어있는 서포터 프로필을 함께 생성할 때 사용
     */
    public static SupporterProfile createEmpty(User user) {
        return new SupporterProfile(user);
    }

    // =====================================================================
    // JPA 라이프사이클
    // =====================================================================

    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // =====================================================================
    // 프로필 업데이트용 편의 메서드
    // =====================================================================

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

    public void updateAddress1(String address1) {
        this.address1 = address1;
    }

    public void updateAddress2(String address2) {
        this.address2 = address2;
    }

    // ---------- 온보딩 관련 필드들 ----------

    /**
     * 한글 설명: 온보딩 Step1 - 관심 카테고리 JSON 문자열 저장
     */
    public void updateInterests(String interestsJson) {
        this.interests = interestsJson;
    }

    /**
     * 한글 설명: 온보딩 Step1 (선택) - 선호 프로젝트 스타일 JSON 문자열 저장
     */
    public void updatePreferredStyles(String preferredStylesJson) {
        this.preferredStyles = preferredStylesJson;
    }

    public void updateBudgetRange(BudgetRange budgetRange) {
        this.budgetRange = budgetRange;
    }

    public void updateFundingExperience(FundingExperience fundingExperience) {
        this.fundingExperience = fundingExperience;
    }

    public void updateAcquisition(AcquisitionChannel channel, String etc) {
        this.acquisitionChannel = channel;
        this.acquisitionChannelEtc = etc;
    }

    public void updateNotificationPreference(NotificationPreference preference) {
        this.notificationPreference = preference;
    }

    // 관심 카테고리 JSON → List<String>
    public List<String> getInterestCategories() {
        if (interests == null || interests.isEmpty()) return List.of();
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(interests, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            return List.of();
        }
    }

    // 스타일 JSON → List<String>
    public List<String> getPreferredStylesList() {
        if (preferredStyles == null || preferredStyles.isEmpty()) return List.of();
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(preferredStyles, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            return List.of();
        }
    }
}
