package com.moa.backend.domain.user.controller;

import com.moa.backend.domain.user.service.UserService;
import com.moa.backend.global.security.jwt.JwtUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    // 판매자 신청
    @PatchMapping("/apply-creator")
    public ResponseEntity<String> applyForCreator(
            @AuthenticationPrincipal JwtUserPrincipal principal
    ) {
        userService.applyForCreator(principal.getId());
        return ResponseEntity.ok("판매자 승인 요청이 완료되었습니다. 관리자의 승인을 기다려주세요.");
    }
}
