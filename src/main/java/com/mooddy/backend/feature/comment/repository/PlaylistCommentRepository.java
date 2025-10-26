package com.mooddy.backend.feature.comment.repository;

import com.mooddy.backend.feature.comment.domain.PlaylistComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface PlaylistCommentRepository extends JpaRepository<PlaylistComment, Long> {

    @EntityGraph(attributePaths = {"user"})
    Page<PlaylistComment> findByPlaylistIdAndParentIsNull(Long playlistId, Pageable pageable);

    @EntityGraph(attributePaths = {"user"})
    List<PlaylistComment> findByParentIdInOrderByCreatedAtAsc(Collection<Long> parentIds);

    @EntityGraph(attributePaths = {"user", "playlist"})
    Optional<PlaylistComment> findByIdAndPlaylistId(Long id, Long playlistId);
}

