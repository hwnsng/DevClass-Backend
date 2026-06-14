package com.hwnsng.devclass.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException e, HttpServletRequest req) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(f -> f.getField() + ": " + f.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("VALIDATION_ERROR", message).withPath(req.getRequestURI()));
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustom(CustomException e, HttpServletRequest req) {
        log.warn("[{}] {} - {}", req.getMethod(), req.getRequestURI(), e.getMessage());
        ErrorResponse body = new ErrorResponse(e.getCode(), e.getMessage())
                .withPath(req.getRequestURI());
        return ResponseEntity.status(e.getStatus()).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAll(Exception e, HttpServletRequest req) {
        // 원인 체인에서 root cause 추출
        Throwable root = e;
        while (root.getCause() != null) root = root.getCause();
        String detail = root.getClass().getSimpleName() + ": " + root.getMessage();

        log.error("[{}] {} → {}", req.getMethod(), req.getRequestURI(), detail, e);

        ErrorResponse body = new ErrorResponse("INTERNAL_ERROR", detail)
                .withPath(req.getRequestURI());
        return ResponseEntity.internalServerError().body(body);
    }
}