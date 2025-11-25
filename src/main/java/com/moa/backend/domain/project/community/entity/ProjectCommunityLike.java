package com.moa.backend.domain.project.community.entity;

import com.moa.backend.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "project_community_like")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectCommunityLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 어떤 커뮤니티 글에 눌린 좋아요인지
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community_id", nullable = false)
    private ProjectCommunity community;

    // 누가 좋아요를 눌렀는지
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
