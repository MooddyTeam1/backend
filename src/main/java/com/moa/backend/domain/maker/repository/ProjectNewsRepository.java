package com.moa.backend.domain.maker.repository;

import com.moa.backend.domain.maker.entity.ProjectNews;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProjectNewsRepository extends JpaRepository<ProjectNews, Long> {

    // 한글 설명: 한 프로젝트의 소식을 pinned(상단고정) 우선, 그 다음 최신순으로 정렬
    List<ProjectNews> findByProject_IdOrderByPinnedDescCreatedAtDesc(Long projectId);

    // 한글 설명: 메이커 콘솔용 - 페이지네이션 + 검색 + 기간 필터
    @Query("""
        SELECT n
        FROM ProjectNews n
        WHERE n.project.id = :projectId
          AND (:keyword IS NULL 
               OR LOWER(n.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(n.content) LIKE LOWER(CONCAT('%', :keyword, '%')))
          AND (:from IS NULL OR n.createdAt >= :from)
          AND (:to   IS NULL OR n.createdAt <= :to)
        ORDER BY n.pinned DESC, n.createdAt DESC
        """)
    Page<ProjectNews> searchProjectNewsForMaker(
            @Param("projectId") Long projectId,
            @Param("keyword") String keyword,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            Pageable pageable
    );
}
