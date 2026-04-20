package com.dhaliwal.notemind.service.impl;

import com.dhaliwal.notemind.service.ImageManagerService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;

@Service
public class ImageManagerServiceImpl implements ImageManagerService {
    @Override
    public String getUrlFromImage(MultipartFile file) {
        String uploadPath = System.getProperty("user.dir") + "/uploads";

        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

        File destination = new File(uploadDir, fileName);

        try {
            file.transferTo(destination);
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file", e);
        }

        return "http://localhost:8080/api/images/" + fileName;
    }
}
