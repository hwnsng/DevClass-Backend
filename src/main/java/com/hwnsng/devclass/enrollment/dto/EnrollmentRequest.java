package com.hwnsng.devclass.enrollment.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentRequest {
    @NotNull
    private Long userId;
    @NotNull
    private Long courseId;
}