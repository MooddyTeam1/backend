package com.moa.backend.domain.follow.entity;

import com.moa.backend.domain.user.entity.SupporterProfile;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static jakarta.persistence.FetchType.LAZY;

/**
 * 서포터 ↔ 서포터 팔로우 (일방향: follower -> following)
 */
@Entity
@Table(
        name = "supporter_follows_supporter",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_supporter_follow_supporter",
                        columnNames = {"follower_user_id", "following_user_id"}
                )
        }
)
@Getter
@NoArgsConstructor
public class SupporterFollowSupporter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 팔로우 하는 서포터
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "follower_user_id", referencedColumnName = "user_id", nullable = false)
    private SupporterProfile follower;

    // 팔로우 당하는 서포터
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "following_user_id", referencedColumnName = "user_id", nullable = false)
    private SupporterProfile following;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private SupporterFollowSupporter(SupporterProfile follower, SupporterProfile following) {
        this.follower = follower;
        this.following = following;
    }

    public static SupporterFollowSupporter of(SupporterProfile follower, SupporterProfile following) {
        return new SupporterFollowSupporter(follower, following);
    }
}
