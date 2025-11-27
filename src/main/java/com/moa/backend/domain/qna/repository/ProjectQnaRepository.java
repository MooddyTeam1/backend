package com.moa.backend.domain.qna.repository;

import com.moa.backend.domain.qna.entity.ProjectQna;
import com.moa.backend.domain.qna.entity.ProjectQnaStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectQnaRepository extends JpaRepository<ProjectQna, Long> {

    // 메이커 관리/프로젝트 상세에서 사용하는 기본 목록
    List<ProjectQna> findByProject_IdOrderByCreatedAtDesc(Long projectId);

    // 필요 시: 특정 유저가 남긴 Q&A 목록
    List<ProjectQna> findByProject_IdAndQuestioner_IdOrderByCreatedAtDesc(Long projectId, Long questionerId);

    // 한글 설명: 특정 프로젝트의 "특정 상태" Q&A 목록 (예: PENDING만)
    List<ProjectQna> findByProject_IdAndStatusOrderByCreatedAtDesc(
            Long projectId,
            ProjectQnaStatus status
    );

    // 공개 Q&A(비공개 아님) 목록
    List<ProjectQna> findByProject_IdAndIsPrivateFalseOrderByCreatedAtDesc(Long projectId);

    // ==============================
    // 🔥 페이지네이션용 메서드 추가
    // ==============================

    // 한글 설명: 프로젝트 전체 Q&A (페이지네이션)
    Page<ProjectQna> findByProject_IdOrderByCreatedAtDesc(Long projectId, Pageable pageable);

    // 한글 설명: 프로젝트의 특정 상태(PENDING 등) Q&A (페이지네이션)
    Page<ProjectQna> findByProject_IdAndStatusOrderByCreatedAtDesc(
            Long projectId,
            ProjectQnaStatus status,
            Pageable pageable
    );
}
