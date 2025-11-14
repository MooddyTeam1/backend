// 한글 설명: 서포터 프로필 기준으로 프로젝트를 찜(북마크)한 정보를 저장하는 엔티티.
package com.moa.backend.domain.follow.entity;

import com.moa.backend.domain.project.entity.Project;
import com.moa.backend.domain.user.entity.SupporterProfile;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Table(
        name = "supporter_bookmarks_project",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_supporter_bookmark_project",
                        columnNames = {"supporter_user_id", "project_id"}
                )
        }
)
@Getter
@NoArgsConstructor
public class SupporterBookmarkProject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 한글 설명: 찜을 한 서포터 프로필 (supporter_profiles.user_id 기준 FK).
    @ManyToOne(fetch = LAZY, optional = false)
    @JoinColumn(name = "supporter_user_id", referencedColumnName = "user_id", nullable = false)
    private SupporterProfile supporter;

    // 한글 설명: 찜 대상 프로젝트.
    @ManyToOne(fetch = LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // 한글 설명: 외부에서 직접 new 하지 않고 정적 팩토리 메서드로만 생성.
    private SupporterBookmarkProject(SupporterProfile supporter, Project project) {
        this.supporter = supporter;
        this.project = project;
    }

    public static SupporterBookmarkProject of(SupporterProfile supporter, Project project) {
        return new SupporterBookmarkProject(supporter, project);
    }
}
