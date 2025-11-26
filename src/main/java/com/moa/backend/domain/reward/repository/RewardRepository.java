package com.moa.backend.domain.reward.repository;

import com.moa.backend.domain.project.entity.Project;
import com.moa.backend.domain.reward.entity.Reward;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 리워드 조회/검색 전용 리포지토리.
 */
public interface RewardRepository extends JpaRepository<Reward, Long> {

    /** 프로젝트 내 특정 리워드 ID 목록 조회 */
    List<Reward> findByProjectIdAndIdIn(Long projectId, Collection<Long> rewardIds);

    /** 프로젝트와 리워드 ID를 함께 검증 */
    Optional<Reward> findByIdAndProjectId(Long rewardId, Long projectId);

    void deleteByProject(Project project);

    // 한글 설명: 프로젝트 ID 기준으로 해당 프로젝트에 속한 리워드 전체 조회
    // - Maker 프로젝트 관리화면에서 리워드 목록/요약 정보 조회용
    List<Reward> findByProject_Id(Long projectId);

}

