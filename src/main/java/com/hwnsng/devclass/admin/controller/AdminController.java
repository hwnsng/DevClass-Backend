package com.hwnsng.devclass.admin.controller;

import com.hwnsng.devclass.common.exception.CustomException;
import com.hwnsng.devclass.course.service.CourseService;
import com.hwnsng.devclass.course.entity.Course;
import com.hwnsng.devclass.course.repository.CourseRepository;
import com.hwnsng.devclass.course.dto.CourseListResponse;
import com.hwnsng.devclass.enrollment.repository.EnrollmentRepository;
import com.hwnsng.devclass.external.EmailService;
import com.hwnsng.devclass.notification.service.NotificationService;
import com.hwnsng.devclass.payment.entity.PaymentItem;
import com.hwnsng.devclass.payment.entity.PaymentItemStatus;
import com.hwnsng.devclass.payment.entity.PaymentStatus;
import com.hwnsng.devclass.payment.repository.PaymentItemRepository;
import com.hwnsng.devclass.payment.service.TossPaymentClient;
import com.hwnsng.devclass.payment.repository.PaymentRepository;
import com.hwnsng.devclass.report.repository.CourseReportRepository;
import com.hwnsng.devclass.user.entity.User;
import com.hwnsng.devclass.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final CourseService courseService;
    private final EnrollmentRepository enrollmentRepository;
    private final NotificationService notificationService;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PaymentItemRepository paymentItemRepository;
    private final TossPaymentClient tossPaymentClient;
    private final CourseRepository courseRepository;
    private final PaymentRepository paymentRepository;
    private final CourseReportRepository reportRepository;

    @DeleteMapping("/courses/{courseId}")
    public ResponseEntity<Void> deleteCourse(
            @PathVariable Long courseId,
            @RequestParam(required = false, defaultValue = "관리자에 의해 삭제되었습니다.") String reason) {

        // 강의 정보 조회 (삭제 전)
        String courseTitle;
        int coursePrice;
        try {
            var detail = courseService.getCourse(courseId);
            courseTitle = detail.getTitle();
            coursePrice = detail.getPrice() != null ? detail.getPrice() : 0;
        } catch (Exception e) {
            courseTitle = "강의";
            coursePrice = 0;
        }

        // 수강 중인 학생 목록 조회
        List<Long> enrolledUserIds = enrollmentRepository
                .findStudentsByCourseId(courseId)
                .stream()
                .map(v -> v.getUserId())
                .toList();

        final String finalTitle = courseTitle;
        final int finalPrice = coursePrice;

        enrolledUserIds.forEach(userId -> {
            // 앱 알림
            notificationService.send(userId,
                    "수강 강의 삭제 안내",
                    String.format("수강 중이던 강의 '%s'가 관리자에 의해 삭제되었습니다. 사유: %s", finalTitle, reason));

            // 유료 강의 결제 내역 조회 → 환불
            userRepository.findById(userId).ifPresent(user -> {
                emailService.sendCourseDeletedNotification(user.getEmail(), user.getName(), finalTitle, reason);

                if (finalPrice > 0) {
                    List<PaymentItem> paidItems = paymentItemRepository.findByCourseIdAndStatus(
                            courseId, PaymentItemStatus.ACTIVE);

                    paidItems.stream()
                            .filter(item -> item.getPayment() != null
                                    && item.getPayment().getUserId().equals(userId)
                                    && item.getPayment().getStatus() == PaymentStatus.PAID)
                            .forEach(item -> {
                                try {
                                    tossPaymentClient.cancel(
                                            item.getPayment().getTossPaymentKey(),
                                            "관리자 강의 삭제 환불: " + reason,
                                            item.getAmount()
                                    );
                                    item.refund("관리자 강의 삭제: " + reason);
                                    paymentItemRepository.save(item);
                                    emailService.sendRefundNotification(
                                            user.getEmail(), user.getName(), finalTitle, item.getAmount());
                                    log.info("Refund processed for userId={} courseId={} amount={}",
                                            userId, courseId, item.getAmount());
                                } catch (Exception e) {
                                    log.error("Refund failed for userId={} courseId={}: {}",
                                            userId, courseId, e.getMessage());
                                }
                            });
                }
            });
        });

        // 강의 삭제
        courseService.deleteCourse(courseId);
        log.info("Admin deleted course '{}' (id={}) reason='{}'", finalTitle, courseId, reason);
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
        log.info("Admin deactivated userId={}", userId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/users/{userId}/activate")
    public ResponseEntity<Void> activateUser(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "NOT_FOUND", "존재하지 않는 사용자입니다."));
        user.activate();
        userRepository.save(user);
        log.info("Admin activated userId={}", userId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/courses/{courseId}/status")
    public ResponseEntity<Void> updateCourseStatus(
            @PathVariable Long courseId,
            @RequestBody Map<String, String> body) {
        try {
            courseService.updateStatus(courseId, Course.CourseStatus.valueOf(body.getOrDefault("status", "")));
        } catch (IllegalArgumentException e) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "INVALID_STATUS", "유효하지 않은 강의 상태입니다.");
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> dashboard() {
        long activeUsers = userRepository.findAll().stream()
                .filter(user -> user.getStatus() == User.UserStatus.ACTIVE)
                .count();
        long pendingCourses = courseRepository.findAll().stream()
                .filter(course -> course.getStatus() == Course.CourseStatus.PENDING)
                .count();
        long paidRevenue = paymentRepository.findAll().stream()
                .filter(payment -> payment.getStatus() == PaymentStatus.PAID)
                .mapToLong(payment -> payment.getTotalAmount())
                .sum();
        return ResponseEntity.ok(Map.of(
                "users", userRepository.count(),
                "activeUsers", activeUsers,
                "courses", courseRepository.count(),
                "pendingCourses", pendingCourses,
                "reports", reportRepository.count(),
                "payments", paymentRepository.count(),
                "paidRevenue", paidRevenue
        ));
    }

    @GetMapping("/courses")
    public ResponseEntity<List<CourseListResponse>> getCourses() {
        return ResponseEntity.ok(courseRepository.findAll().stream().map(courseService::toListResponse).toList());
    }
}
