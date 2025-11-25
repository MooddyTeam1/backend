package com.moa.backend.domain.project.community.repository;

import com.moa.backend.domain.project.community.entity.ProjectCommunityComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectCommunityCommentRepository extends JpaRepository<ProjectCommunityComment, Long> {
    List<ProjectCommunityComment> findByCommunity_IdOrderByCreatedAtAsc(Long communityId);
}
