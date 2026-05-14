package com.hwnsng.devclass.admin.controller;

import com.hwnsng.devclass.common.exception.CustomException;
import com.hwnsng.devclass.course.service.CourseService;
import com.hwnsng.devclass.enrollment.repository.EnrollmentRepository;
import com.hwnsng.devclass.notification.service.NotificationService;
import com.hwnsng.devclass.user.entity.User;
import com.hwnsng.devclass.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final CourseService courseService;
    private final EnrollmentRepository enrollmentRepository;
    private final NotificationService notificationService;
    private final UserRepository userRepository;

    @DeleteMapping("/courses/{courseId}")
    public ResponseEntity<Void> deleteCourse(
            @PathVariable Long courseId,
            @RequestParam(required = false, defaultValue = "관리자에 의해 삭제되었습니다.") String reason) {

        // 강의 제목 조회 (삭제 전)
        String courseTitle;
        try {
            courseTitle = courseService.getCourse(courseId).getTitle();
        } catch (Exception e) {
            courseTitle = "강의";
        }

        // 수강 중인 학생 목록 조회 (삭제 전)
        List<Long> enrolledUserIds = enrollmentRepository
                .findStudentsByCourseId(courseId)
                .stream()
                .map(v -> v.getUserId())
                .toList();

        // 학생들에게 알림 발송
        String finalTitle = courseTitle;
        enrolledUserIds.forEach(userId ->
                notificationService.send(userId,
                        "수강 강의 삭제 안내",
                        String.format("수강 중이던 강의 '%s'가 관리자에 의해 삭제되었습니다. 사유: %s", finalTitle, reason))
        );

        // 강의 삭제
        courseService.deleteCourse(courseId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/users")
    public ResponseEntity<List<Map<String, Object>>> getUsers() {
        List<Map<String, Object>> users = userRepository.findAll().stream()
                .map(u -> Map.of(
                        "id", (Object) u.getId(),
                        "email", u.getEmail(),
                        "name", u.getName(),
                        "role", u.getRole().name(),
                        "status", u.getStatus().name(),
                        "createdAt", u.getCreatedAt() != null ? u.getCreatedAt().toString() : ""
                ))
                .toList();
        return ResponseEntity.ok(users);
    }

    @PutMapping("/users/{userId}/deactivate")
    public ResponseEntity<Void> deactivateUser(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "NOT_FOUND", "존재하지 않는 사용자입니다."));
        user.deactivate();
        userRepository.save(user);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/users/{userId}/activate")
    public ResponseEntity<Void> activateUser(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "NOT_FOUND", "존재하지 않는 사용자입니다."));
        user.activate();
        userRepository.save(user);
        return ResponseEntity.noContent().build();
    }
}
