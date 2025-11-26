package com.moa.backend.domain.makernews.entity;

import com.moa.backend.domain.maker.entity.Maker;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 한글 설명: 메이커 홈에 노출되는 '메이커 소식' 엔티티.
 * - 메이커가 자신의 프로필/홈에서 올리는 공지, 이벤트, 신제품 소식 등을 나타낸다.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "maker_news")
public class MakerNews {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    // 한글 설명: 메이커 소식 PK
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maker_id", nullable = false)
    // 한글 설명: 소식을 작성한 메이커 (makers.id 참조)
    private Maker maker;

    @Column(name = "title", nullable = false, length = 200)
    // 한글 설명: 소식 제목 (최대 200자)
    private String title;

    @Lob
    @Column(name = "content_markdown", nullable = false)
    // 한글 설명: 마크다운 형식의 소식 내용
    private String contentMarkdown;

    @Enumerated(EnumType.STRING)
    @Column(name = "news_type", nullable = false, length = 30)
    // 한글 설명: 소식 유형 (EVENT, NOTICE, NEW_PRODUCT)
    private MakerNewsType newsType;

    @Column(name = "created_at", nullable = false)
    // 한글 설명: 생성일시
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    // 한글 설명: 수정일시
    private LocalDateTime updatedAt;

    // === 정적 팩토리 메서드 ===

    /**
     * 한글 설명: 메이커 소식 생성용 정적 팩토리.
     */
    public static MakerNews create(Maker maker,
                                   String title,
                                   String contentMarkdown,
                                   MakerNewsType newsType) {
        MakerNews news = new MakerNews();
        news.maker = maker;
        news.title = title;
        news.contentMarkdown = contentMarkdown;
        news.newsType = (newsType != null) ? newsType : MakerNewsType.NOTICE;
        LocalDateTime now = LocalDateTime.now();
        news.createdAt = now;
        news.updatedAt = now;
        return news;
    }

    /**
     * 한글 설명: 메이커 소식 수정(제목/내용/유형)을 수행한다.
     */
    public void update(String title,
                       String contentMarkdown,
                       MakerNewsType newsType) {
        this.title = title;
        this.contentMarkdown = contentMarkdown;
        if (newsType != null) {
            this.newsType = newsType;
        }
        this.updatedAt = LocalDateTime.now();
    }
}
