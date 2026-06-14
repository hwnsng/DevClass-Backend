package com.hwnsng.devclass.question.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record QuestionCreateRequest(
        @NotBlank @Size(max = 160) String title,
        @NotBlank @Size(max = 4000) String content
) {}
