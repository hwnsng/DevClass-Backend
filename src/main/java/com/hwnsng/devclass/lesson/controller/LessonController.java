package com.hwnsng.devclass.lesson.controller;

import com.hwnsng.devclass.lesson.dto.LessonResponse;
import com.hwnsng.devclass.lesson.service.LessonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Tag(name = "Lesson", description = "레슨 API")
@RestController
@RequiredArgsConstructor
public class LessonController {

    private final LessonService lessonService;

    @Operation(summary = "레슨 생성", description = "강의에 새 레슨을 추가하고 영상을 업로드합니다.")
    @PostMapping(value = "/api/courses/{courseId}/lessons", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<LessonResponse> createLesson(
            @PathVariable Long courseId,
            @RequestParam String title,
            @RequestParam(required = false) String description,
            @RequestParam int lessonOrder,
            @RequestParam MultipartFile file
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(lessonService.createLesson(courseId, title, description, lessonOrder, file));
    }

    @Operation(summary = "레슨 수정", description = "레슨 제목, 설명, 또는 영상을 수정합니다.")
    @PutMapping("/api/lessons/{lessonId}")
    public ResponseEntity<LessonResponse> updateLesson(
            @PathVariable Long lessonId,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) MultipartFile file
    ) {
        return ResponseEntity.ok(lessonService.updateLesson(lessonId, title, description, file));
    }

    @Operation(summary = "레슨 삭제")
    @DeleteMapping("/api/lessons/{lessonId}")
    public ResponseEntity<Void> deleteLesson(@PathVariable Long lessonId) {
        lessonService.deleteLesson(lessonId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "레슨 영상 스트리밍", description = "Range 요청을 지원합니다.")
    @GetMapping("/api/lessons/{lessonId}/video")
    public ResponseEntity<ResourceRegion> streamVideo(
            @PathVariable Long lessonId,
            @RequestHeader HttpHeaders headers
    ) throws IOException {
        Resource video = lessonService.getVideoResource(lessonId);
        long contentLength = video.contentLength();

        List<HttpRange> ranges = headers.getRange();
        ResourceRegion region;
        HttpStatus status;

        if (ranges.isEmpty()) {
            long rangeLength = Math.min(2 * 1024 * 1024L, contentLength);
            region = new ResourceRegion(video, 0, rangeLength);
            status = HttpStatus.OK;
        } else {
            HttpRange range = ranges.get(0);
            long start = range.getRangeStart(contentLength);
            long end = range.getRangeEnd(contentLength);
            long rangeLength = Math.min(2 * 1024 * 1024L, end - start + 1);
            region = new ResourceRegion(video, start, rangeLength);
            status = HttpStatus.PARTIAL_CONTENT;
        }

        return ResponseEntity.status(status)
                .contentType(MediaType.parseMediaType("video/mp4"))
                .header(HttpHeaders.ACCEPT_RANGES, "bytes")
                .body(region);
    }
}
