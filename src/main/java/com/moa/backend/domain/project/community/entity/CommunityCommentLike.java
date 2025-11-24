package com.moa.backend.domain.project.community.entity;

import com.moa.backend.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "community_comment_like")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommunityCommentLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private ProjectCommunityComment comment;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
}
