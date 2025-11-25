package com.moa.backend.domain.qna.entity;

import com.moa.backend.domain.project.entity.Project;
import com.moa.backend.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 한글 설명: 프로젝트 Q&A 엔티티
 * - 서포터가 프로젝트 상세에서 남기는 문의
 * - 메이커가 답변(선택)
 */
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "project_qna")
public class ProjectQna {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 어떤 프로젝트의 Q&A 인지
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    // 질문자 (User = Supporter)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "questioner_id", nullable = false)
    private User questioner;

    // 질문 내용
    @Column(name = "question", nullable = false, columnDefinition = "TEXT")
    private String question;

    // 답변 내용 (없을 수 있음)
    @Column(name = "answer", columnDefinition = "TEXT")
    private String answer;

    // 상태 (PENDING / ANSWERED)
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ProjectQnaStatus status;

    // 비공개 여부 (true면 나 + 메이커만)
    @Column(name = "is_private", nullable = false)
    private boolean isPrivate;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "answered_at")
    private LocalDateTime answeredAt;

    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // ====== 비즈니스 메서드 ======

    /**
     * 한글 설명: 메이커가 답변을 등록/수정할 때 사용
     */
    public void answer(String answerText) {
        this.answer = answerText;
        this.status = ProjectQnaStatus.ANSWERED;
        this.answeredAt = LocalDateTime.now();
    }
}
