package com.hwnsng.devclass.review.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ReviewRequest {
    @NotNull
    private Long userId;
    @NotNull @Min(1) @Max(5)
    private Integer rating;
    private String content;
}
