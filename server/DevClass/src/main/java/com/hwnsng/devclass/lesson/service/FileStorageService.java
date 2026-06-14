package com.hwnsng.devclass.lesson.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    String store(MultipartFile file, Long courseId, Long lessonId);
    String storeThumbnail(MultipartFile file, Long courseId);
    void delete(String relativePath);
    Resource getResource(String relativePath);
}
