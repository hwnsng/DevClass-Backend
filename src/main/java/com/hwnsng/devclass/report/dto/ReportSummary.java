package com.hwnsng.devclass.report.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.List;

@Getter
@AllArgsConstructor
public class ReportSummary {
    private Long courseId;
    private String courseTitle;
    private long reportCount;
    private List<ReportResponse> details;
}
