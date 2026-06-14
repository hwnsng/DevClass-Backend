package com.hwnsng.devclass.report.dto;

import lombok.Getter;

@Getter
public class ReportRequest {
    private Long userId;
    private String reason;
    private String description;
}
