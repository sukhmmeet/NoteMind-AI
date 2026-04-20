package com.dhaliwal.notemind.service.impl;

import com.cloudinary.Cloudinary;
import com.dhaliwal.notemind.service.ImageManagerService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.util.Map;

@Service
public class ImageManagerServiceImpl implements ImageManagerService {
    private final Cloudinary cloudinary;

    public ImageManagerServiceImpl(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    @Override
    public String getUrlFromImage(MultipartFile file) {
        try {
            Map uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    Map.of("folder", "notes")
            );

            return uploadResult.get("secure_url").toString();

        } catch (IOException e) {
            throw new RuntimeException("Image upload failed", e);
        }
    }
}
