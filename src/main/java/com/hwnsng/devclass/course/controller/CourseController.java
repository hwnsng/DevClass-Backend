package com.hwnsng.devclass.course.controller;

import com.hwnsng.devclass.course.dto.*;
import com.hwnsng.devclass.course.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Tag(name = "Course", description = "강의 API")
@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @Operation(summary = "강의 목록 조회", description = "키워드 검색, 페이징, 정렬(latest/popular), 강사 필터 지원")
    @GetMapping
    public ResponseEntity<?> getCourses(
            @Parameter(description = "검색 키워드") @RequestParam(required = false) String query,
            @Parameter(description = "페이지 번호 (1부터 시작)") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "정렬 기준: latest(최신순), popular(인기순)") @RequestParam(defaultValue = "latest") String sort,
            @Parameter(description = "강사 ID 필터") @RequestParam(required = false) Long instructorId
    ) {
        Page<CourseListResponse> result = courseService.getCourses(query, page, size, sort, instructorId);
        return ResponseEntity.ok(Map.of(
                "items", result.getContent(),
                "page", Map.of(
                        "page", page,
                        "size", size,
                        "total", result.getTotalElements()
                )
        ));
    }

    @Operation(summary = "강의 상세 조회")
    @GetMapping("/{courseId}")
    public ResponseEntity<CourseDetailResponse> getCourse(
            @Parameter(description = "강의 ID") @PathVariable Long courseId
    ) {
        return ResponseEntity.ok(courseService.getCourse(courseId));
    }

    @Operation(summary = "강의 생성")
    @PostMapping
    public ResponseEntity<CourseDetailResponse> createCourse(@RequestBody CreateCourseRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(courseService.createCourse(request));
    }

    @Operation(summary = "강의 수정")
    @PutMapping("/{courseId}")
    public ResponseEntity<CourseDetailResponse> updateCourse(
            @PathVariable Long courseId,
            @RequestBody UpdateCourseRequest request
    ) {
        return ResponseEntity.ok(courseService.updateCourse(courseId, request));
    }

    @Operation(summary = "강의 썸네일 업로드", description = "jpg, png, webp 이미지 (최대 10MB)")
    @PostMapping(value = "/{courseId}/thumbnail", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CourseDetailResponse> uploadThumbnail(
            @PathVariable Long courseId,
            @RequestParam MultipartFile file
    ) {
        return ResponseEntity.ok(courseService.uploadThumbnail(courseId, file));
    }

    @Operation(summary = "강의 삭제 (강사 본인 또는 관리자)")
    @DeleteMapping("/{courseId}")
    public ResponseEntity<Void> deleteCourse(
            @PathVariable Long courseId,
            @RequestParam Long instructorId
    ) {
        courseService.deleteCourseByInstructor(courseId, instructorId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "강의 썸네일 조회")
    @GetMapping("/{courseId}/thumbnail")
    public ResponseEntity<Resource> getThumbnail(@PathVariable Long courseId) {
        Resource resource = courseService.getThumbnailResource(courseId);
        String filename = resource.getFilename() != null ? resource.getFilename().toLowerCase() : "";
        MediaType contentType = filename.endsWith(".png") ? MediaType.IMAGE_PNG
                : filename.endsWith(".webp") ? MediaType.parseMediaType("image/webp")
                : MediaType.IMAGE_JPEG;
        return ResponseEntity.ok().contentType(contentType).body(resource);
    }
}
