package com.moa.backend.domain.notification.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "notice")
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long receiverId;

    private String title;

    private String message;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    @Enumerated(EnumType.STRING)
    private NotificationTargetType targetType;

    private Long targetId;

    @Builder.Default
    private boolean read = false;

    @CreationTimestamp
    private LocalDateTime createdAt;

    public void markAsRead() {
        this.read = true;
    }
}
