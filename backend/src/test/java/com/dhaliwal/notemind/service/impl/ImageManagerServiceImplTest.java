package com.dhaliwal.notemind.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImageManagerServiceImplTest {

    @Mock
    private Cloudinary cloudinary;

    @Mock
    private Uploader uploader;

    @InjectMocks
    private ImageManagerServiceImpl imageManagerService;

    @BeforeEach
    void setUp() {
        when(cloudinary.uploader()).thenReturn(uploader);
    }

    @Test
    void shouldUploadImageAndReturnSecureUrl() throws Exception {

        MockMultipartFile file = new MockMultipartFile(
                "image",
                "test.png",
                "image/png",
                "dummy-image".getBytes()
        );

        when(uploader.upload(any(byte[].class), anyMap()))
                .thenReturn(
                        Map.of(
                                "secure_url",
                                "https://res.cloudinary.com/demo/image/upload/test.png"
                        )
                );

        String url = imageManagerService.uploadAndGetUrl(file);

        assertEquals(
                "https://res.cloudinary.com/demo/image/upload/test.png",
                url
        );

        verify(uploader).upload(any(byte[].class), anyMap());
    }

    @Test
    void shouldThrowRuntimeExceptionWhenUploadFails() throws Exception {

        MockMultipartFile file = new MockMultipartFile(
                "image",
                "test.png",
                "image/png",
                "dummy-image".getBytes()
        );

        when(uploader.upload(any(byte[].class), anyMap()))
                .thenThrow(new IOException("Upload failed"));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> imageManagerService.uploadAndGetUrl(file)
        );

        assertEquals("Image upload failed", exception.getMessage());
        assertInstanceOf(IOException.class, exception.getCause());

        verify(uploader).upload(any(byte[].class), anyMap());
    }
}