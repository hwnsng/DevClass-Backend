package com.hwnsng.devclass.progress.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;

@Getter
public class ProgressUpdateRequest {
    @NotNull
    @Min(0) @Max(100)
    private Integer percent;

    private Long lastLessonId;
}