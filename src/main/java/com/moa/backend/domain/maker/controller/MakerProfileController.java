package com.moa.backend.domain.maker.controller;

import com.moa.backend.domain.maker.dto.MakerProfileResponse;
import com.moa.backend.domain.maker.dto.MakerProfileUpdateRequest;
import com.moa.backend.domain.maker.service.MakerProfileService;
import com.moa.backend.global.security.jwt.JwtUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/makers/me")
@RequiredArgsConstructor
public class MakerProfileController {

    private final MakerProfileService makerProfileService;

    @GetMapping
    public ResponseEntity<MakerProfileResponse> getProfile(
            @AuthenticationPrincipal JwtUserPrincipal principal
    ) {
        MakerProfileResponse response = makerProfileService.getProfile(principal.getId());
        return ResponseEntity.ok(response);
    }

    @PatchMapping
    public ResponseEntity<MakerProfileResponse> updateProfile(
            @AuthenticationPrincipal JwtUserPrincipal principal,
            @RequestBody MakerProfileUpdateRequest request
    ) {
        MakerProfileResponse response = makerProfileService.updateProfile(principal.getId(), request);
        return ResponseEntity.ok(response);
    }
}