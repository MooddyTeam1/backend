package com.example.mooddy.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
public class ImageService {
    // 로컬 파일 시스템 경로 설정 (실제 환경에 맞게 변경 필요)
    private final String uploadDir = "uploads/";

    public String upload(MultipartFile file) {
        if (file.isEmpty()) return null;
        try {
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();

            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
            File dest = new File(dir, filename);
            file.transferTo(dest);

            return "/uploads/" + filename;
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload image", e);
        }
    }

    public void delete(String imageUrl) {
        if (imageUrl == null || !imageUrl.startsWith("/uploads/")) return;

        // 파일 이름 추출 및 삭제
        String filename = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
        File file = new File(uploadDir + filename);
        if (file.exists()) file.delete();
    }
}