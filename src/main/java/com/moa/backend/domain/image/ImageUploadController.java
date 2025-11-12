// src/main/java/com/moa/backend/domain/image/ImageUploadController.java
package com.moa.backend.domain.image;

import com.moa.backend.global.file.LocalFileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 공용 이미지 업로드 API 컨트롤러
 * - 현재는 용도 구분 없이 단일 이미지 업로드로 사용
 * - 나중에 usage 파라미터를 열어서 ImageUsage 기반 정책을 적용할 수 있음
 */
@RestController
@RequestMapping("/api/uploads")
@RequiredArgsConstructor
public class ImageUploadController {

    private final LocalFileStorageService fileStorageService;

    /**
     * 이미지 업로드 엔드포인트
     * 예)
     *  - POST /api/uploads/images
     *  - multipart/form-data 형식으로 file 전송
     *
     *  나중에 용도 구분을 추가하고 싶으면:
     *  - POST /api/uploads/images?usage=PROFILE
     *  - @RequestParam("usage") ImageUsage usage 파라미터를 다시 살리면 됨
     */
    @PostMapping("/images")
    public ResponseEntity<ImageUploadResponse> uploadImage(
            @RequestParam("file") MultipartFile file
            // 🔴 지금은 사용 안 하는 용도 파라미터 (필요해지면 주석 해제)
            // @RequestParam("usage") ImageUsage usage
    ) throws IOException {

        // ✅ 파일 유효성 검증 (비어있지 않은지)
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        // ✅ 로컬 디스크에 저장하고 URL 반환받기 (현재는 용도 구분 없음)
        String url = fileStorageService.storeImage(file);

        // 🔴 용도까지 고려한 버전 예시 (지금은 미사용)
        // String url = fileStorageService.storeImage(file, usage);

        ImageUploadResponse response = new ImageUploadResponse(
                url,
                file.getOriginalFilename(),
                file.getSize()
        );

        return ResponseEntity.ok(response);
    }
    @PostMapping("/images/batch")
    public ResponseEntity<List<ImageUploadResponse>> uploadImages(
            @RequestParam("files") List<MultipartFile> files // ✅ 여러 개 받기
    ) throws IOException {

        // ✅ 파일 리스트 검증 (비어있는지 체크)
        if (files == null || files.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        List<ImageUploadResponse> responses = new ArrayList<>();

        for (MultipartFile file : files) {
            // ✅ 빈 파일은 스킵 (원하면 에러로 처리해도 됨)
            if (file.isEmpty()) {
                continue;
            }

            // ✅ 기존 단일 저장 메서드 재사용
            String url = fileStorageService.storeImage(file);

            ImageUploadResponse response = new ImageUploadResponse(
                    url,
                    file.getOriginalFilename(),
                    file.getSize()
            );

            responses.add(response);
        }

        return ResponseEntity.ok(responses);
    }
}
