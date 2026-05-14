package com.hwnsng.devclass.lesson.service;

import com.hwnsng.devclass.common.exception.CustomException;
import com.hwnsng.devclass.course.entity.Course;
import com.hwnsng.devclass.course.repository.CourseRepository;
import com.hwnsng.devclass.lesson.dto.LessonResponse;
import com.hwnsng.devclass.lesson.entity.Lesson;
import com.hwnsng.devclass.lesson.repository.LessonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional
public class LessonService {

    private final LessonRepository lessonRepository;
    private final CourseRepository courseRepository;
    private final FileStorageService fileStorageService;

    public LessonResponse createLesson(Long courseId, String title, String description, int lessonOrder, MultipartFile file) {
        validateMp4(file);

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "NOT_FOUND", "존재하지 않는 강의입니다."));

        Lesson lesson = Lesson.create(course, title, description, lessonOrder);
        lesson = lessonRepository.save(lesson);

        String relativePath = fileStorageService.store(file, courseId, lesson.getId());
        lesson.updateVideo(relativePath, file.getOriginalFilename(), file.getSize());

        return new LessonResponse(lesson);
    }

    public LessonResponse updateLesson(Long lessonId, String title, String description, MultipartFile file) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "NOT_FOUND", "존재하지 않는 레슨입니다."));

        if (title != null && !title.isBlank()) {
            lesson.updateTitle(title);
        }

        if (description != null) {
            lesson.updateDescription(description);
        }

        if (file != null && !file.isEmpty()) {
            validateMp4(file);
            fileStorageService.delete(lesson.getVideoUrl());
            String relativePath = fileStorageService.store(file, lesson.getCourse().getId(), lessonId);
            lesson.updateVideo(relativePath, file.getOriginalFilename(), file.getSize());
        }

        return new LessonResponse(lesson);
    }

    public void deleteLesson(Long lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "NOT_FOUND", "존재하지 않는 레슨입니다."));
        fileStorageService.delete(lesson.getVideoUrl());
        lessonRepository.delete(lesson);
    }

    @Transactional(readOnly = true)
    public Resource getVideoResource(Long lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "NOT_FOUND", "존재하지 않는 레슨입니다."));

        if (lesson.getVideoUrl() == null) {
            throw new CustomException(HttpStatus.NOT_FOUND, "NO_VIDEO", "업로드된 영상이 없습니다.");
        }

        Resource resource = fileStorageService.getResource(lesson.getVideoUrl());
        if (!resource.exists()) {
            throw new CustomException(HttpStatus.NOT_FOUND, "FILE_NOT_FOUND", "영상 파일을 찾을 수 없습니다.");
        }
        return resource;
    }

    private void validateMp4(MultipartFile file) {
        String name = file.getOriginalFilename();
        if (name == null || !name.toLowerCase().endsWith(".mp4")) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "INVALID_FORMAT", "MP4 파일만 업로드 가능합니다.");
        }
        if (file.getSize() > 500L * 1024 * 1024) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "FILE_TOO_LARGE", "파일 크기는 500MB 이하여야 합니다.");
        }
    }
}
