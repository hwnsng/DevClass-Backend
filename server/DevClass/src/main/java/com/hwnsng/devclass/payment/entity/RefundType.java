package com.hwnsng.devclass.payment.entity;

public enum RefundType {
    USER_REQUEST,           // 사용자 요청
    ENROLLMENT_EXCEEDED,    // 수강 인원 초과
    COURSE_CANCELLED,       // 강의 취소
    INSUFFICIENT_ENROLLMENT // 수강 인원 미달로 강의 취소
}
