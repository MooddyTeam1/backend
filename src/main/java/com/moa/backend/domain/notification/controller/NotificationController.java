package com.moa.backend.domain.notification.controller;

import com.moa.backend.domain.notification.dto.NotificationResponse;
import com.moa.backend.domain.notification.entity.Notification;
import com.moa.backend.domain.notification.entity.NotificationTargetType;
import com.moa.backend.domain.notification.entity.NotificationType;
import com.moa.backend.domain.notification.service.NotificationService;
import com.moa.backend.domain.notification.sse.service.SseNotificationService;
import com.moa.backend.global.security.jwt.JwtTokenProvider;
import com.moa.backend.global.security.jwt.JwtUserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notifications")
@Tag(name = "Notification", description = "알림 구독/조회/읽음 처리")
public class NotificationController {

    private final NotificationService notificationService;
    private final SseNotificationService sseNotificationService;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * SSE 구독 (실시간 알림 수신)
     * 로그인 한 유저가 구독하면 서버가 알림을 push함
     */
    @GetMapping(value = "/subscribe", produces = "text/event-stream")
    @Operation(summary = "알림 SSE 구독")
    public SseEmitter subscribe(@RequestParam String token) {
        Long userId = jwtTokenProvider.getUserId(token);
        return sseNotificationService.connect(userId);
    }

    // 알림 목록 전체 조회 (최신순)
    @GetMapping
    @Operation(summary = "알림 전체 조회")
    public ResponseEntity<List<NotificationResponse>> getAll(
            @AuthenticationPrincipal JwtUserPrincipal principal
            ) {
        return ResponseEntity.ok(notificationService.getAll(principal.getId()));
    }

    // 읽지 않은 알림 갯수 조회
    @GetMapping("/unread-count")
    @Operation(summary = "읽지 않은 알림 개수 조회")
    public ResponseEntity<Integer> unreadCount(
            @AuthenticationPrincipal JwtUserPrincipal principal
    ) {
        return ResponseEntity.ok(notificationService.getUnreadCount(principal.getId()));
    }

    // 읽지 않은 알림 전체 읽음 처리
    @PatchMapping("/read/all")
    @Operation(summary = "알림 전체 읽음 처리")
    public ResponseEntity<Void> readAll(
            @AuthenticationPrincipal JwtUserPrincipal principal
    ) {
        notificationService.readAll(principal.getId());
        return ResponseEntity.ok().build();
    }

    // 읽지 않은 알림 읽음 처리
    @PatchMapping("/read/{id}")
    @Operation(summary = "알림 단건 읽음 처리")
    public ResponseEntity<Void> readOne(
            @Parameter(example = "1") @PathVariable Long id,
            @AuthenticationPrincipal JwtUserPrincipal principal
    ) {
        notificationService.readOne(id, principal.getId());
        return ResponseEntity.ok().build();
    }

    //테스트용
    @PostMapping("/test/preparing/{orderId}")
    public void testPreparing(@PathVariable Long orderId) {
        notificationService.send(
                1000L,
                "배송 준비중",
                "주문하신 상품이 곧 출고될 예정입니다.",
                NotificationType.SUPPORTER,
                NotificationTargetType.ORDER,
                orderId
        );
    }
}
