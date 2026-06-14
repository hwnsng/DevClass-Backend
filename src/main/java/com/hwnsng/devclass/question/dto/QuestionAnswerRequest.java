package com.hwnsng.devclass.question.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record QuestionAnswerRequest(
        @NotBlank @Size(max = 4000) String answer
) {}
