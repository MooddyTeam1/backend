///////////////////////////////////////////////////////////////////////////////
// 6. 뷰 집계 레포지토리 (선택 사항: 배치 집계용)
//    파일: com/moa/backend/domain/tracking/repository/ProjectViewStatRepository.java
///////////////////////////////////////////////////////////////////////////////

package com.moa.backend.domain.tracking.repository;

import com.moa.backend.domain.tracking.entity.ProjectViewStat;
import com.moa.backend.domain.tracking.entity.ProjectViewWindowType;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 한글 설명: ProjectViewStat 집계 테이블 레포지토리
 *
 * - 지금 당장은 안 써도 되고,
 *   나중에 스케줄러로 집계할 때 활용.
 */
public interface ProjectViewStatRepository extends JpaRepository<ProjectViewStat, Long> {

    List<ProjectViewStat> findByWindowTypeAndWindowStartAtBetween(
            ProjectViewWindowType windowType,
            LocalDateTime start,
            LocalDateTime end
    );
}