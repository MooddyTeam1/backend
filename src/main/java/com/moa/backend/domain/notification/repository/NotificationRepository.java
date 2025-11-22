package com.moa.backend.domain.notification.repository;

import com.moa.backend.domain.notification.entity.Notification;
import com.moa.backend.domain.notification.entity.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByReceiverIdAndReadFalse(Long receiverId);

    // 특정 사용자의 전체 알림 조회 (최신순)
    List<Notification> findByReceiverIdOrderByCreatedAtDesc(Long receiverId);

    // 타입별 조회 (Maker, Supporter 필터링)
    List<Notification> findByReceiverIdAndTypeOrderByCreatedAtDesc(Long receiverId, NotificationType type);
}
