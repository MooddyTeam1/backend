package com.moa.backend.domain.follow.entity;

import com.moa.backend.domain.maker.entity.Maker;
import com.moa.backend.domain.user.entity.SupporterProfile;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static jakarta.persistence.FetchType.LAZY;

/**
 * 서포터 → 메이커 팔로우
 */
@Entity
@Table(
        name = "supporter_follows_maker",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_supporter_follow_maker",
                        columnNames = {"supporter_user_id", "maker_id"}
                )
        }
)
@Getter
@NoArgsConstructor
public class SupporterFollowMaker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 팔로우 하는 서포터
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "supporter_user_id", referencedColumnName = "user_id", nullable = false)
    private SupporterProfile supporter;

    // 팔로우 당하는 메이커
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "maker_id", nullable = false)
    private Maker maker;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private SupporterFollowMaker(SupporterProfile supporter, Maker maker) {
        this.supporter = supporter;
        this.maker = maker;
    }

    public static SupporterFollowMaker of(SupporterProfile supporter, Maker maker) {
        return new SupporterFollowMaker(supporter, maker);
    }
}
