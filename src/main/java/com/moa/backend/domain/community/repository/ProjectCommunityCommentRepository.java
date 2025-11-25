package com.moa.backend.domain.community.repository;

import com.moa.backend.domain.community.entity.ProjectCommunityComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectCommunityCommentRepository extends JpaRepository<ProjectCommunityComment, Long> {
    List<ProjectCommunityComment> findByCommunity_IdOrderByCreatedAtAsc(Long communityId);
}
