// src/main/java/com/moa/backend/global/file/LocalFileStorageProperties.java
package com.moa.backend.global.file;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 로컬 파일 저장 관련 경로 설정을 관리하는 클래스
 * - application.yml 의 app.file-storage.* 값을 주입받아 사용
 */
@Component
@ConfigurationProperties(prefix = "app.file-storage")
public class LocalFileStorageProperties {

    // ✅ 파일이 저장될 루트 디렉터리 (예: ./uploads)
    private String rootDir;

    // ✅ 이미지가 저장될 하위 디렉터리 (예: images)
    private String imageDir;

    public String getRootDir() {
        return rootDir;
    }

    public void setRootDir(String rootDir) {
        this.rootDir = rootDir;
    }

    public String getImageDir() {
        return imageDir;
    }

    public void setImageDir(String imageDir) {
        this.imageDir = imageDir;
    }
}
