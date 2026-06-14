package com.hwnsng.devclass.question.controller;

import com.hwnsng.devclass.question.dto.QuestionAnswerRequest;
import com.hwnsng.devclass.question.dto.QuestionCreateRequest;
import com.hwnsng.devclass.question.dto.QuestionResponse;
import com.hwnsng.devclass.question.service.CourseQuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class CourseQuestionController {

    private final CourseQuestionService questionService;

    @PostMapping("/api/courses/{courseId}/questions")
    public ResponseEntity<QuestionResponse> create(
            @PathVariable Long courseId,
            Authentication authentication,
            @Valid @RequestBody QuestionCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(questionService.create(courseId, (Long) authentication.getPrincipal(), request));
    }

    @GetMapping("/api/courses/{courseId}/questions")
    public ResponseEntity<Map<String, Object>> getCourseQuestions(
            @PathVariable Long courseId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<QuestionResponse> result = questionService.getCourseQuestions(courseId, page, size);
        return ResponseEntity.ok(Map.of("items", result.getContent(), "total", result.getTotalElements()));
    }

    @GetMapping("/api/instructor/questions")
    public ResponseEntity<Map<String, Object>> getInstructorQuestions(
            Authentication authentication,
            @RequestParam(required = false) Long courseId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "50") int size) {
        Page<QuestionResponse> result = questionService.getInstructorQuestions(
                (Long) authentication.getPrincipal(), courseId, page, size);
        return ResponseEntity.ok(Map.of("items", result.getContent(), "total", result.getTotalElements()));
    }

    @PutMapping("/api/questions/{questionId}/answer")
    public ResponseEntity<QuestionResponse> answer(
            @PathVariable Long questionId,
            Authentication authentication,
            @Valid @RequestBody QuestionAnswerRequest request) {
        boolean admin = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
        return ResponseEntity.ok(questionService.answer(
                questionId, (Long) authentication.getPrincipal(), admin, request));
    }
}
