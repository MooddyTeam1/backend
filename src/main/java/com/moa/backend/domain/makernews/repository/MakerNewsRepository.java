package com.moa.backend.domain.makernews.repository;

import com.moa.backend.domain.makernews.entity.MakerNews;
import com.moa.backend.domain.makernews.entity.MakerNewsType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 한글 설명: 메이커 소식 JPA 레포지토리.
 */
public interface MakerNewsRepository extends JpaRepository<MakerNews, Long> {

    // 한글 설명: 특정 메이커의 소식을 최신순으로 페이징 조회.
    Page<MakerNews> findByMakerIdOrderByCreatedAtDesc(Long makerId, Pageable pageable);

    // 한글 설명: 특정 메이커의 특정 유형 소식을 최신순으로 페이징 조회.
    Page<MakerNews> findByMakerIdAndNewsTypeOrderByCreatedAtDesc(
            Long makerId,
            MakerNewsType newsType,
            Pageable pageable
    );

    // 한글 설명: 소식 ID와 메이커 ID로 조회(소유자 검증용).
    Optional<MakerNews> findByIdAndMakerId(Long id, Long makerId);
}
