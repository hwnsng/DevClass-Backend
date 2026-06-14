package com.hwnsng.devclass.enrollment.controller;

import com.hwnsng.devclass.enrollment.dto.EnrollmentRequest;
import com.hwnsng.devclass.enrollment.dto.EnrollmentResponse;
import com.hwnsng.devclass.enrollment.service.EnrollmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;

@Tag(name = "Enrollment", description = "수강 API")
@RestController
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @Operation(summary = "수강 등록", description = "강의를 수강 등록합니다. 중복 등록 시 409 반환")
    @PostMapping("/api/enrollments")
    public ResponseEntity<EnrollmentResponse> enroll(
            @Valid @RequestBody EnrollmentRequest req
    ) {
        EnrollmentResponse res = enrollmentService.enroll(req);
        return ResponseEntity
                .created(URI.create("/api/enrollments/" + res.getEnrollmentId()))
                .body(res);
    }

    @Operation(summary = "내 수강 목록 조회", description = "사용자의 수강 중인 강의 목록을 페이징 조회합니다.")
    @GetMapping("/api/users/{userId}/enrollments")
    public ResponseEntity<?> getMyEnrollments(
            @Parameter(description = "사용자 ID") @PathVariable Long userId,
            @Parameter(description = "페이지 번호") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "20") int size
    ) {
        Page<EnrollmentResponse> result = enrollmentService.getMyEnrollments(userId, page, size);
        return ResponseEntity.ok(Map.of(
                "items", result.getContent(),
                "page", Map.of(
                        "page", page,
                        "size", size,
                        "total", result.getTotalElements()
                )
        ));
    }

    @Operation(summary = "강의 수강생 목록 조회", description = "강사용: 특정 강의의 수강생 목록과 진도율을 조회합니다.")
    @GetMapping("/api/courses/{courseId}/students")
    public ResponseEntity<List<?>> getCourseStudents(
            @Parameter(description = "강의 ID") @PathVariable Long courseId
    ) {
        return ResponseEntity.ok(enrollmentService.getCourseStudents(courseId));
    }

    @Operation(summary = "수강 취소", description = "수강을 취소합니다. (상태를 CANCELLED로 변경)")
    @DeleteMapping("/api/enrollments/{enrollmentId}")
    public ResponseEntity<Void> cancel(
            @Parameter(description = "수강 ID") @PathVariable Long enrollmentId,
            @Parameter(description = "사용자 ID (P1 임시)") @RequestParam Long userId
    ) {
        enrollmentService.cancel(enrollmentId, userId);
        return ResponseEntity.noContent().build();
    }
}