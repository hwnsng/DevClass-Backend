package com.hwnsng.devclass.enrollment.repository;

public interface CourseStudentView {
    Long getUserId();
    String getName();
    String getEnrolledAt();
    Integer getProgressPercent();
}
