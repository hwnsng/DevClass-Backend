package com.hwnsng.devclass.report.service;

import com.hwnsng.devclass.common.exception.CustomException;
import com.hwnsng.devclass.course.repository.CourseRepository;
import com.hwnsng.devclass.report.dto.ReportRequest;
import com.hwnsng.devclass.report.dto.ReportResponse;
import com.hwnsng.devclass.report.dto.ReportSummary;
import com.hwnsng.devclass.report.entity.CourseReport;
import com.hwnsng.devclass.report.repository.CourseReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final CourseReportRepository reportRepository;
    private final CourseRepository courseRepository;

    @Transactional
    public ReportResponse createReport(Long courseId, ReportRequest req) {
        if (!courseRepository.existsById(courseId)) {
            throw new CustomException(HttpStatus.NOT_FOUND, "COURSE_NOT_FOUND", "강의를 찾을 수 없습니다.");
        }
        if (reportRepository.existsByUserIdAndCourseId(req.getUserId(), courseId)) {
            throw new CustomException(HttpStatus.CONFLICT, "REPORT_DUPLICATE", "이미 신고한 강의입니다.");
        }
        if (req.getReason() == null || req.getReason().isBlank()) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "REASON_REQUIRED", "신고 사유를 입력해주세요.");
        }
        CourseReport report = CourseReport.create(req.getUserId(), courseId, req.getReason(), req.getDescription());
        return new ReportResponse(reportRepository.save(report));
    }

    @Transactional(readOnly = true)
    public List<ReportSummary> getAdminReports() {
        List<Object[]> counts = reportRepository.countByCourse();
        Map<Long, Long> countMap = new LinkedHashMap<>();
        for (Object[] row : counts) {
            countMap.put((Long) row[0], (Long) row[1]);
        }

        return countMap.entrySet().stream()
                .sorted(Map.Entry.<Long, Long>comparingByValue().reversed())
                .map(entry -> {
                    Long courseId = entry.getKey();
                    long cnt = entry.getValue();
                    String title = courseRepository.findById(courseId)
                            .map(c -> c.getTitle()).orElse("(삭제된 강의)");
                    List<ReportResponse> details = reportRepository.findByCourseId(courseId).stream()
                            .map(ReportResponse::new)
                            .collect(Collectors.toList());
                    return new ReportSummary(courseId, title, cnt, details);
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReportResponse> getCourseReports(Long courseId) {
        return reportRepository.findByCourseId(courseId).stream()
                .map(ReportResponse::new)
                .collect(Collectors.toList());
    }
}
