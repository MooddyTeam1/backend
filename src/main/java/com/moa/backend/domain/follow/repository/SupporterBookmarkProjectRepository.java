// 한글 설명: 서포터 → 프로젝트 찜(북마크) 레포지토리.
package com.moa.backend.domain.follow.repository;

import com.moa.backend.domain.follow.entity.SupporterBookmarkProject;
import com.moa.backend.domain.project.entity.Project;
import com.moa.backend.domain.user.entity.SupporterProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SupporterBookmarkProjectRepository
        extends JpaRepository<SupporterBookmarkProject, Long> {

    // 한글 설명: 특정 서포터가 해당 프로젝트를 찜했는지 여부.
    boolean existsBySupporterAndProject(SupporterProfile supporter, Project project);

    // 한글 설명: 특정 프로젝트를 찜한 서포터 수.
    long countByProject(Project project);

    // 한글 설명: 서포터-프로젝트 찜 엔티티 단건 조회 (해제할 때 사용).
    Optional<SupporterBookmarkProject> findBySupporterAndProject(SupporterProfile supporter, Project project);

    // 한글 설명: 서포터가 찜한 모든 프로젝트 목록 조회용.
    List<SupporterBookmarkProject> findBySupporter(SupporterProfile supporter);
}
