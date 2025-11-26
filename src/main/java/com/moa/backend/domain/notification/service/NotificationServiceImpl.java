package com.moa.backend.domain.notification.service;


import com.moa.backend.domain.notification.dto.NotificationResponse;
import com.moa.backend.domain.notification.entity.Notification;
import com.moa.backend.domain.notification.entity.NotificationTargetType;
import com.moa.backend.domain.notification.entity.NotificationType;
import com.moa.backend.domain.notification.repository.NotificationRepository;
import com.moa.backend.domain.notification.sse.service.SseNotificationService;
import com.moa.backend.global.error.AppException;
import com.moa.backend.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final SseNotificationService sseNotificationService;

    // 알림 전송
    @Override
    @Transactional
    public Notification send(Long receiverId, String title, String message, NotificationType type,
                             NotificationTargetType targetType, Long targetId) {
        Notification notification = Notification.builder()
                .receiverId(receiverId)
                .title(title)
                .message(message)
                .type(type)
                .targetId(targetId)
                .targetType(targetType)
                .build();

        notificationRepository.save(notification);

        NotificationResponse response = NotificationResponse.from(notification);
        sseNotificationService.send(receiverId, response);

        return notification;

    }

    // 읽지 않은 알림 갯수
    @Override
    public int getUnreadCount(Long receiverId) {
        return notificationRepository.findByReceiverIdAndReadFalse(receiverId).size();
    }

    // 읽지 않은 알림 전체 읽음 처리
    @Override
    public void readAll(Long receiverId) {
        var notifications = notificationRepository.findByReceiverIdAndReadFalse(receiverId);
        notifications.forEach(notification -> {
            notification.markAsRead();
        });
    }

    // 읽지 않음 알림 읽음 처리
    @Override
    public void readOne(Long notificationId, Long receiverId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new AppException(ErrorCode.NOTIFICATION_NOT_FOUND));

        if (!notification.getReceiverId().equals(receiverId)) {
            throw new AppException(ErrorCode.NOTIFICATION_NOT_READ);
        }

        notification.markAsRead();
    }

    // 알림 전체 조회 (최신순)
    @Override
    public List<NotificationResponse> getAll(Long receiverId) {
        return notificationRepository.findByReceiverIdOrderByCreatedAtDesc(receiverId)
                .stream()
                .map(NotificationResponse::from)
                .toList();
    }
}
