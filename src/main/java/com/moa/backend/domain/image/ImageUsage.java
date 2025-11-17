// src/main/java/com/moa/backend/domain/image/ImageUsage.java
package com.moa.backend.domain.image;

/**
 * 이미지 사용 용도를 나타내는 enum
 * - 프로필 이미지인지, 프로젝트 커버 이미지인지 등을 구분
 */
public enum ImageUsage {
    PROFILE,        // 프로필 이미지
    PROJECT_COVER,  // 프로젝트 커버 이미지
    PROJECT_GALLERY // 프로젝트 갤러리 이미지
}
