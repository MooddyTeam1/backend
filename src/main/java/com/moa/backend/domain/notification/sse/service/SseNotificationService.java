package com.moa.backend.domain.notification.sse.service;

import com.moa.backend.domain.notification.dto.NotificationResponse;
import com.moa.backend.domain.notification.sse.repository.SseEmitterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
@RequiredArgsConstructor
public class SseNotificationService {

    private final SseEmitterRepository sseEmitterRepository;

    /**
     * SSE 연결 생성 (유저 구독)
     */
    public SseEmitter connect(Long userId) {
        // 1시간 동안 연결 유지
        SseEmitter emitter = new SseEmitter(60L * 1000 * 60);

        sseEmitterRepository.save(userId, emitter);

        // 최초 연결 시 더미 이벤트 한 번 보내기 (연결 확인 용도)
        try {
            emitter.send(
                    SseEmitter
                            .event()
                            .name("connect")
                            .data("connected")
            );
        } catch (Exception e) {
            sseEmitterRepository.delete(userId);
        }

        emitter.onCompletion(() -> sseEmitterRepository.delete(userId));
        emitter.onTimeout(()-> sseEmitterRepository.delete(userId));

        return emitter;
    }

    /**
     * 실시간 알림 전송
     */
    public void send(Long userId, NotificationResponse response) {

        SseEmitter emitter = sseEmitterRepository.get(userId);

        if(emitter != null) {
            try {
                emitter.send(
                        SseEmitter
                                .event()
                                .name("notification")
                                .data(response)
                );
            } catch (Exception e) {
                // 전송 중 문제가 생긴 Emitter는 제거 (죽은 연결)
                sseEmitterRepository.delete(userId);
            }
        }
    }
}
