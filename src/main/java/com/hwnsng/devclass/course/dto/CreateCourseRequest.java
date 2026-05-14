package com.hwnsng.devclass.course.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateCourseRequest {
    private Long instructorId;
    private String title;
    private String description;
    private Integer price;
}
