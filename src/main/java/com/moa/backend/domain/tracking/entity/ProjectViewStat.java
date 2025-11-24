///////////////////////////////////////////////////////////////////////////////
// 4. 프로젝트 뷰 집계 엔티티 (선택 사항: 나중에 배치로 채우는 테이블)
//    파일: com/moa/backend/domain/tracking/entity/ProjectViewStat.java
///////////////////////////////////////////////////////////////////////////////

package com.moa.backend.domain.tracking.entity;

import com.moa.backend.domain.project.entity.Project;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;

/**
 * 한글 설명: 프로젝트별/시간 구간별 뷰 집계 테이블
 *
 * 예시:
 *  - windowType = HOUR, windowStartAt = 2025-11-24T15:00 -> 15~16시 사이 뷰 수
 *  - windowType = DAY,  windowStartAt = 2025-11-24T00:00 -> 하루 동안 뷰 수
 *
 * 지금 당장 안 써도 되고, 나중에 통계/리포트 최적화용으로 쓰면 됨.
 */
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "project_view_stats", indexes = {
        @Index(name = "idx_view_stat_project_window", columnList = "project_id, window_type, window_start_at")
})
public class ProjectViewStat {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "project_view_stat_id_seq")
    @SequenceGenerator(name = "project_view_stat_id_seq", sequenceName = "project_view_stat_id_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    @Comment("집계 대상 프로젝트")
    private Project project;

    @Enumerated(EnumType.STRING)
    @Column(name = "window_type", nullable = false, length = 20)
    @Comment("집계 윈도우 타입 (HOUR/DAY/WEEK)")
    private ProjectViewWindowType windowType;

    @Column(name = "window_start_at", nullable = false)
    @Comment("집계 윈도우 시작 시각")
    private LocalDateTime windowStartAt;

    @Column(name = "view_count", nullable = false)
    @Comment("해당 구간 총 뷰 수")
    private Long viewCount;

    @Column(name = "unique_user_count")
    @Comment("해당 구간 고유 유저 수(선택)")
    private Long uniqueUserCount;

    @Column(name = "last_aggregated_at", nullable = false)
    @Comment("집계가 마지막으로 업데이트된 시각")
    private LocalDateTime lastAggregatedAt;

    @PrePersist
    protected void onCreate() {
        if (this.lastAggregatedAt == null) {
            this.lastAggregatedAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.lastAggregatedAt = LocalDateTime.now();
    }
}