package com.hwnsng.devclass.report.controller;

import com.hwnsng.devclass.report.dto.ReportRequest;
import com.hwnsng.devclass.report.dto.ReportResponse;
import com.hwnsng.devclass.report.dto.ReportSummary;
import com.hwnsng.devclass.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    // 강의 신고 (인증된 사용자)
    @PostMapping("/api/courses/{courseId}/reports")
    public ResponseEntity<ReportResponse> report(@PathVariable Long courseId,
                                                  @RequestBody ReportRequest req) {
        return ResponseEntity.ok(reportService.createReport(courseId, req));
    }

    // 관리자: 전체 신고 목록
    @GetMapping("/api/admin/reports")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ReportSummary>> getAdminReports() {
        return ResponseEntity.ok(reportService.getAdminReports());
    }

    // 관리자: 특정 강의 신고 상세
    @GetMapping("/api/admin/reports/{courseId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ReportResponse>> getCourseReports(@PathVariable Long courseId) {
        return ResponseEntity.ok(reportService.getCourseReports(courseId));
    }
}
