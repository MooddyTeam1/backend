package com.moa.backend.domain.community.repository;

import com.moa.backend.domain.community.entity.ProjectCommunityLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectCommunityLikeRepository extends JpaRepository<ProjectCommunityLike, Long> {

    // 특정 유저가 특정 커뮤니티 글에 이미 좋아요 눌렀는지 확인
    boolean existsByCommunity_IdAndUser_Id(Long communityId, Long userId);

    // 특정 유저가 특정 커뮤니티 글에 눌렀던 좋아요 삭제
    void deleteByCommunity_IdAndUser_Id(Long communityId, Long userId);

    // 해당 커뮤니티 글의 총 좋아요 개수
    long countByCommunity_Id(Long communityId);
}
