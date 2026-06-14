package com.hwnsng.devclass.course.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateCourseRequest {
    @NotNull
    private Long instructorId;

    @NotBlank @Size(min = 2, max = 200)
    private String title;

    @Size(max = 2000)
    private String description;

    @NotNull @Min(0)
    private Integer price;
}
