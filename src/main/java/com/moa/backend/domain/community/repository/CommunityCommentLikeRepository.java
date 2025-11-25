package com.moa.backend.domain.community.repository;

import com.moa.backend.domain.community.entity.CommunityCommentLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommunityCommentLikeRepository extends JpaRepository<CommunityCommentLike, Long> {

    boolean existsByComment_IdAndUser_Id(Long commentId, Long userId);

    void deleteByComment_IdAndUser_Id(Long commentId, Long userId);

    long countByComment_Id(Long commentId);
}


