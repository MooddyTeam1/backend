// src/main/java/com/moa/backend/domain/image/ImageUploadResponse.java
package com.moa.backend.domain.image;

/**
 * 이미지 업로드 API 응답 DTO
 * - 프론트에 최종 이미지 URL 등을 전달하기 위해 사용
 */
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "이미지 업로드 응답")
public record ImageUploadResponse(
        @Schema(description = "업로드된 이미지 URL", example = "https://cdn.moa.dev/uploads/img1.png")
        String url,          // 업로드된 이미지에 접근 가능한 URL
        @Schema(description = "원본 파일명", example = "photo.png")
        String originalName, // 원본 파일명
        @Schema(description = "파일 크기(Byte)", example = "123456")
        long size            // 파일 크기 (byte)
) {
}
