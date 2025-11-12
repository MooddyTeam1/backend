// src/main/java/com/moa/backend/domain/image/ImageUploadResponse.java
package com.moa.backend.domain.image;

/**
 * 이미지 업로드 API 응답 DTO
 * - 프론트에 최종 이미지 URL 등을 전달하기 위해 사용
 */
public record ImageUploadResponse(
        String url,          // 업로드된 이미지에 접근 가능한 URL
        String originalName, // 원본 파일명
        long size            // 파일 크기 (byte)
) {
}
