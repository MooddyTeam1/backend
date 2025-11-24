///////////////////////////////////////////////////////////////////////////////
// 2. 트래킹 이벤트 엔티티
//    파일: com/moa/backend/domain/tracking/entity/TrackingEvent.java
///////////////////////////////////////////////////////////////////////////////

package com.moa.backend.domain.tracking.entity;

import com.moa.backend.domain.maker.entity.Maker;
import com.moa.backend.domain.project.entity.Project;
import com.moa.backend.domain.user.entity.SupporterProfile;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;

/**
 * 한글 설명: 개별 사용자 행동(페이지뷰, 프로젝트뷰 등)을 기록하는 이벤트 테이블
 *
 * 특징:
 *  - 너무 세밀하게 잡으면 데이터 폭발하니, 최소한으로 필요하다고 생각되는 필드만 우선 설계
 *  - 나중에 필요하면 extraJson에 추가로 정보를 넣거나, 컬럼을 더 늘리면 됨
 */
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tracking_events", indexes = {
        @Index(name = "idx_tracking_event_type_time", columnList = "event_type, occurred_at"),
        @Index(name = "idx_tracking_project_time", columnList = "project_id, occurred_at"),
        @Index(name = "idx_tracking_user_time", columnList = "supporter_id, occurred_at"),
        @Index(name = "idx_tracking_session_time", columnList = "session_id, occurred_at")
})
public class TrackingEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tracking_event_id_seq")
    @SequenceGenerator(name = "tracking_event_id_seq", sequenceName = "tracking_event_id_seq", allocationSize = 1)
    private Long id;

    /**
     * 한글 설명: 이벤트를 발생시킨 유저 (비로그인일 수도 있으므로 nullable)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supporter_id")
    @Comment("이벤트를 발생시킨 유저 (비로그인 가능)")
    private SupporterProfile  supporter;

    /**
     * 한글 설명: 연관된 프로젝트(프로젝트 뷰, 카드뷰, 후원 버튼 클릭 등일 때)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    @Comment("연관된 프로젝트 (있을 경우에만)")
    private Project project;

    /**
     * 한글 설명: 연관된 메이커(메이커 페이지 트래킹 등 필요 시 사용)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maker_id")
    @Comment("연관된 메이커 (있을 경우에만)")
    private Maker maker;

    /**
     * 한글 설명: 세션 단위 식별자 (프론트에서 쿠키/로컬스토리지 등으로 생성해서 보내주는 값)
     */
    @Column(name = "session_id", length = 100)
    @Comment("클라이언트 세션 식별자")
    private String sessionId;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false, length = 50)
    @Comment("이벤트 타입 (페이지뷰, 프로젝트뷰, 후원버튼 클릭 등)")
    private TrackingEventType eventType;

    @Column(name = "path", length = 512)
    @Comment("요청된 URL path (예: /project/123)")
    private String path;

    @Column(name = "referrer", length = 512)
    @Comment("이전 페이지/유입 경로 (HTTP Referer 등)")
    private String referrer;

    @Column(name = "user_agent", length = 1024)
    @Comment("브라우저 User-Agent")
    private String userAgent;

    @Column(name = "client_ip", length = 100)
    @Comment("클라이언트 IP (X-Forwarded-For 등에서 추출)")
    private String clientIp;

    @Column(name = "occurred_at", nullable = false)
    @Comment("이벤트 발생 시각")
    private LocalDateTime occurredAt;

    @Column(name = "extra_json", columnDefinition = "TEXT")
    @Comment("추가 데이터(JSON 문자열, 실험/버전 정보 등)")
    private String extraJson;

    @PrePersist
    protected void onCreate() {
        if (this.occurredAt == null) {
            this.occurredAt = LocalDateTime.now();
        }
    }
}