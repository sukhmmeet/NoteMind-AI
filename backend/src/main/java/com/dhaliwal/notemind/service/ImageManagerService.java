package com.dhaliwal.notemind.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface ImageManagerService {
    String getUrlFromImage(MultipartFile file);
}
