package com.mooddy.backend.feature.user.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
public class ImageService {

    private final String uploadDir = "uploads/";

    public String upload(MultipartFile file) {
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
        if (imageUrl == null) return;

        File file = new File(uploadDir + imageUrl.substring(imageUrl.lastIndexOf("/") + 1));
        if (file.exists()) file.delete();
    }
}
