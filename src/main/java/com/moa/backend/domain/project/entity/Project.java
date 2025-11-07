package com.moa.backend.domain.project.entity;

import com.moa.backend.domain.user.entity.User;
import com.moa.backend.global.converter.StringListConverter;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Table(name = "project")
@Builder
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "maker_user_id", nullable = false)
    private User maker;

    @Column(name = "title", nullable = true, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = true)
    private String summary;

    @Column(columnDefinition = "TEXT", nullable = true)
    private String storyMarkdown;

    @Column(name = "goal_amount", nullable = true)
    private Long goalAmount;

    @Column(name = "start_at", nullable = true)
    private LocalDate startDate;    //날짜만

    @Column(name = "end_at", nullable = true)
    private LocalDate endDate;      //날짜만

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private Category category;

    // 날짜 기반 자동 업데이트
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "lifecycle_status", nullable = false, length = 20)
    private ProjectLifecycleStatus lifecycleStatus = ProjectLifecycleStatus.DRAFT;

    // 관리자 심사 상태
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "review_status", nullable = false, length = 20)
    private ProjectReviewStatus reviewStatus = ProjectReviewStatus.NONE;

    private LocalDateTime requestAt;
    private LocalDateTime approvedAt;
    private LocalDateTime rejectedAt;
    private String rejectedReason;

    @Column(length = 512)
    private String coverImageUrl;

    // 현재 h2(개발용)
    // postgres(운영용) = TEXT 옆 (json_valid(cover_gallery))추가 및 data.sql 이중따옴표 제거
    @Column(columnDefinition = "TEXT")
    @Convert(converter = StringListConverter.class)
    private List<String> coverGallery = new ArrayList<>();

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    private LocalDateTime liveStartAt;  //시간까지 00시고정.
    private LocalDateTime liveEndAt;

    @ElementCollection
    @CollectionTable(
            name = "project_tag",
            joinColumns = @JoinColumn(name = "project_id")
    )
    @Column(name = "tag")
    private List<String> tags = new ArrayList<>();

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
