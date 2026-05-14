package com.hwnsng.devclass.lesson.service;

import com.hwnsng.devclass.common.exception.CustomException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;

@Service
public class LocalFileStorageService implements FileStorageService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    public String store(MultipartFile file, Long courseId, Long lessonId) {
        try {
            Path dir = Paths.get(uploadDir, courseId.toString());
            Files.createDirectories(dir);

            String relativePath = courseId + "/" + lessonId + ".mp4";
            Path target = Paths.get(uploadDir).resolve(relativePath);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

            return relativePath;
        } catch (IOException e) {
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, "FILE_SAVE_ERROR", "파일 저장에 실패했습니다.");
        }
    }

    @Override
    public String storeThumbnail(MultipartFile file, Long courseId) {
        try {
            Path dir = Paths.get(uploadDir, "thumbnails");
            Files.createDirectories(dir);

            String originalName = file.getOriginalFilename();
            String ext = (originalName != null && originalName.contains("."))
                    ? originalName.substring(originalName.lastIndexOf('.'))
                    : ".jpg";

            String relativePath = "thumbnails/" + courseId + ext;
            Path target = Paths.get(uploadDir).resolve(relativePath);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

            return relativePath;
        } catch (IOException e) {
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, "FILE_SAVE_ERROR", "썸네일 저장에 실패했습니다.");
        }
    }

    @Override
    public void delete(String relativePath) {
        if (relativePath == null) return;
        try {
            Files.deleteIfExists(Paths.get(uploadDir).resolve(relativePath));
        } catch (IOException ignored) {}
    }

    @Override
    public Resource getResource(String relativePath) {
        Path filePath = Paths.get(uploadDir).resolve(relativePath).normalize().toAbsolutePath();
        return new PathResource(filePath);
    }
}
