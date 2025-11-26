package com.moa.backend.domain.maker.entity;

import com.moa.backend.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

// 한글 설명: 메이커 기본 정보 엔티티 (User와 1:1, 개인/사업자 겸용 구조)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "makers")
public class Maker {

    // 한글 설명: 메이커 PK (현재는 User당 1개만 허용, 향후 1:N로 확장 가능)
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "maker_id_seq")
    @SequenceGenerator(name = "maker_id_seq", sequenceName = "maker_id_seq", allocationSize = 1)
    private Long id;

    // 한글 설명: 메이커 소유자(User). 현재는 1:1, 추후 1:N 구조로 확장 고려.
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_user_id", nullable = false, unique = true)
    private User owner;

    // 한글 설명: 메이커 유형 (개인 / 사업자)
    @Enumerated(EnumType.STRING)
    @Column(name = "maker_type", nullable = false, length = 20)
    private MakerType makerType;

    // ===== 공통 필드 =====

    // 한글 설명: 메이커 이름 (브랜드/스튜디오명)
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    // 한글 설명: 사업자 상호명 (개인 메이커일 경우 name과 동일하게 둘 수 있음)
    @Column(name = "business_name", nullable = false, length = 100)
    private String businessName;

    // 한글 설명: 사업자등록번호 (개인 메이커는 null 또는 공백)
    @Column(name = "business_number", length = 50)
    private String businessNumber;

    // 한글 설명: 대표자명
    @Column(name = "representative", length = 50)
    private String representative;

    // 한글 설명: 설립일
    @Column(name = "established_at")
    private LocalDate establishedAt;

    // 한글 설명: 업종 (예: 스마트 하드웨어)
    @Column(name = "industry_type", length = 100)
    private String industryType;

    // ===== 사업자 전용 필드 =====

    // 한글 설명: 업태 (예: 제조업, 도매 및 소매업)
    @Column(name = "business_item", length = 100)
    private String businessItem;

    // 한글 설명: 통신판매업 신고번호
    @Column(name = "online_sales_registration_no", length = 100)
    private String onlineSalesRegistrationNo;

    // ===== 기타 공통 프로필 정보 =====

    // 한글 설명: 소재지 (예: 서울시 강남구)
    @Column(name = "location", length = 255)
    private String location;

    // 한글 설명: 제품/서비스 소개
    @Column(name = "product_intro", columnDefinition = "TEXT")
    private String productIntro;

    // 한글 설명: 핵심 역량 (텍스트 또는 쉼표 구분 문자열)
    @Column(name = "core_competencies", columnDefinition = "TEXT")
    private String coreCompetencies;

    // 한글 설명: 브랜드 이미지 URL
    @Column(name = "image_url", length = 255)
    private String imageUrl;

    // 한글 설명: 연락 이메일
    @Column(name = "contact_email", length = 100)
    private String contactEmail;

    // 한글 설명: 연락처
    @Column(name = "contact_phone", length = 30)
    private String contactPhone;

    // 한글 설명: 활용 기술 JSON 문자열 (예: ["React","Node.js","AWS"])
    @Column(name = "tech_stack", columnDefinition = "TEXT")
    private String techStackJson;

    // 한글 설명: 키워드 목록 (예: 친환경,소셜임팩트,B2B)
    @Column(name = "keywords", columnDefinition = "TEXT")
    private String keywords;

    // ===== 생성/수정 시각 =====

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // 한글 설명: 빌더 생성자 (필요 필드 중심으로 사용)
    @Builder
    private Maker(
            User owner,
            MakerType makerType,
            String name,
            String businessName,
            String businessNumber,
            String representative,
            LocalDate establishedAt,
            String industryType,
            String businessItem,
            String onlineSalesRegistrationNo,
            String location,
            String productIntro,
            String coreCompetencies,
            String imageUrl,
            String contactEmail,
            String contactPhone,
            String techStackJson,
            String keywords
    ) {
        this.owner = owner;
        this.makerType = makerType;
        this.name = name;
        this.businessName = businessName;
        this.businessNumber = businessNumber;
        this.representative = representative;
        this.establishedAt = establishedAt;
        this.industryType = industryType;
        this.businessItem = businessItem;
        this.onlineSalesRegistrationNo = onlineSalesRegistrationNo;
        this.location = location;
        this.productIntro = productIntro;
        this.coreCompetencies = coreCompetencies;
        this.imageUrl = imageUrl;
        this.contactEmail = contactEmail;
        this.contactPhone = contactPhone;
        this.techStackJson = techStackJson;
        this.keywords = keywords;
    }

    // 한글 설명: 기본 개인 메이커 생성 헬퍼 (사업자 정보 없이)
    public static Maker createIndividual(User owner, String makerName) {
        String baseName = makerName != null ? makerName : owner.getEmail();
        return Maker.builder()
                .owner(owner)
                .makerType(MakerType.INDIVIDUAL)
                .name(baseName)
                .businessName(baseName)
                .contactEmail(owner.getEmail())
                .build();
    }

    // 한글 설명: 기본 사업자 메이커 생성 헬퍼
    public static Maker createBusiness(User owner, String businessName, String businessNumber) {
        String baseName = businessName != null ? businessName : owner.getEmail();
        return Maker.builder()
                .owner(owner)
                .makerType(MakerType.BUSINESS)
                .name(baseName)         // 화면 노출 이름
                .businessName(baseName) // 사업자 상호명과 동일하게 시작
                .businessNumber(businessNumber)
                .contactEmail(owner.getEmail())
                .build();
    }

    // ===== 공통/사업자 정보 일괄 업데이트 메서드 =====

    // 한글 설명: 폼 전체를 기반으로 한 비즈니스 정보 업데이트 메서드
    public void updateBusinessInfo(
            String name,
            String businessName,
            String businessNumber,
            String representative,
            LocalDate establishedAt,
            String industryType,
            String businessItem,
            String onlineSalesRegistrationNo,
            String location,
            String productIntro,
            String coreCompetencies,
            String imageUrl,
            String contactEmail,
            String contactPhone,
            String techStackJson,
            String keywords
    ) {
        this.name = name;
        this.businessName = businessName;
        this.businessNumber = businessNumber;
        this.representative = representative;
        this.establishedAt = establishedAt;
        this.industryType = industryType;
        this.businessItem = businessItem;
        this.onlineSalesRegistrationNo = onlineSalesRegistrationNo;
        this.location = location;
        this.productIntro = productIntro;
        this.coreCompetencies = coreCompetencies;
        this.imageUrl = imageUrl;
        this.contactEmail = contactEmail;
        this.contactPhone = contactPhone;
        this.techStackJson = techStackJson;
        this.keywords = keywords;
    }

    // ===== JPA 라이프사이클 콜백 =====

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // ===== 부분 업데이트 메서드들 (PATCH 용) =====

    // 한글 설명: 메이커 유형 (정책에 따라 수정 허용/불허 결정)
    public void updateMakerType(MakerType makerType) {
        this.makerType = makerType;
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

    public void updateBusinessItem(String businessItem) {
        this.businessItem = businessItem;
    }

    public void updateOnlineSalesRegistrationNo(String no) {
        this.onlineSalesRegistrationNo = no;
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

    public void updateKeywords(String keywords) {
        this.keywords = keywords;
    }
}
