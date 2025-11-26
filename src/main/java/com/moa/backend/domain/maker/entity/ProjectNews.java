package com.moa.backend.domain.maker.entity;

import com.moa.backend.domain.project.entity.Project;
import com.moa.backend.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "project_news")
public class ProjectNews {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 한글 설명: 어떤 프로젝트의 새소식인지
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    // 한글 설명: 작성자 (Maker의 Owner == User ID)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    // 한글 설명: 상단 고정 여부 (메이커 콘솔에서 활용 가능)
    @Builder.Default
    @Column(name = "pinned", nullable = false)
    private boolean pinned = false;

    // 한글 설명: 공개 여부 (true: 서포터에게 노출)
    @Builder.Default
    @Column(name = "is_public", nullable = false)
    private Boolean isPublic = true;

    // 한글 설명: 공지 등록 시 서포터에게 알림 발송 여부
    @Builder.Default
    @Column(name = "notify_supporters", nullable = false)
    private Boolean notifySupporters = false;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 한글 설명: (향후 확장용) 공지에 첨부되는 이미지들
    @OneToMany(mappedBy = "news", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProjectNewsImage> images = new ArrayList<>();

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // 한글 설명: 이미지 추가 편의 메서드
    public void addImage(ProjectNewsImage image) {
        images.add(image);
        image.setNews(this);
    }
}
