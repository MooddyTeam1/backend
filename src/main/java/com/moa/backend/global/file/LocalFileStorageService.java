// src/main/java/com/moa/backend/global/file/LocalFileStorageService.java
package com.moa.backend.global.file;

// import com.moa.backend.domain.image.ImageUsage;  // 🔴 지금은 사용 안 함
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * 로컬 디스크에 파일(이미지)을 저장하는 서비스
 * - 현재는 모든 이미지를 동일한 디렉터리에 저장
 * - 나중에 ImageUsage 를 사용해서 용도별로 분리할 수 있음
 */
@Service
@RequiredArgsConstructor
public class LocalFileStorageService {

    private final LocalFileStorageProperties properties;

    /**
     * 이미지 파일을 로컬 디스크에 저장하고,
     * 클라이언트에서 접근 가능한 URL 경로를 반환하는 메서드
     *
     * @param file 업로드된 MultipartFile
     * @return     예: "/uploads/images/uuid.png" 같은 URL 경로
     */
    public String storeImage(MultipartFile file) throws IOException {
        // ✅ 1. 저장할 루트 디렉터리 (예: ./uploads)
        Path rootPath = Paths.get(properties.getRootDir()).toAbsolutePath().normalize();

        // ✅ 2. 현재는 용도 구분 없이 imageDir만 사용 (예: ./uploads/images)
        Path imageDirPath = rootPath.resolve(properties.getImageDir());

        // 🔴 예전 코드 (용도별 디렉터리 구분) — 지금은 사용 안 함
        // String usageDirName = usage.name().toLowerCase(); // PROFILE -> "profile"
        // Path imageDirPath = rootPath.resolve(properties.getImageDir()).resolve(usageDirName);

        // ✅ 3. 디렉터리가 없으면 생성
        Files.createDirectories(imageDirPath);

        // ✅ 4. 확장자 추출 (원본 파일명에서 마지막 '.' 뒤를 사용)
        String originalFilename = file.getOriginalFilename();
        String ext = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            ext = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        // ✅ 5. 중복 방지를 위해 UUID 기반 파일명 생성
        String filename = UUID.randomUUID() + ext;

        // ✅ 6. 최종 저장 경로
        Path targetPath = imageDirPath.resolve(filename);

        // ✅ 7. 실제 파일 저장
        file.transferTo(targetPath.toFile());

        // ✅ 8. 클라이언트에게 반환할 URL 경로 구성
        //     WebMvcConfig 에서 "/uploads/**" 를 rootDir 로 매핑해두었음
        String urlPath = "/uploads/" + properties.getImageDir() + "/" + filename;
        // 🔴 용도별 디렉터리 사용 버전 예시 (지금은 미사용)
        // String urlPath = "/uploads/" + properties.getImageDir() + "/" + usageDirName + "/" + filename;

        return urlPath;
    }
}
