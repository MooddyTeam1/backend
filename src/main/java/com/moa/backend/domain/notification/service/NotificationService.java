package com.moa.backend.domain.notification.service;

import com.moa.backend.domain.notification.dto.NotificationResponse;
import com.moa.backend.domain.notification.entity.Notification;
import com.moa.backend.domain.notification.entity.NotificationTargetType;
import com.moa.backend.domain.notification.entity.NotificationType;

import java.util.List;

public interface NotificationService {

    // 알림 전송
    Notification send(Long receiverId, String title, String message, NotificationType type,
                      NotificationTargetType targetType, Long targetId);

    // 읽지 않은 알림 갯수
    int getUnreadCount(Long receiverId);

    // 읽지 않은 알림 전체 읽음 처리
    void readAll(Long receiverId);

    // 읽지 않은 알람 읽음 처리
    void readOne(Long notificationId, Long receiverId);

    // 알림 전체 조회
    List<NotificationResponse> getAll(Long receiverId);

}
