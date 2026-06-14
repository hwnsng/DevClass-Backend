package com.hwnsng.devclass.health;

import com.hwnsng.devclass.scheduler.JobRun;
import com.hwnsng.devclass.scheduler.JobRunRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class HealthController {

    private final JobRunRepository jobRunRepository;

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "timestamp", LocalDateTime.now().toString(),
                "service", "devclass-p3"
        ));
    }

    @GetMapping("/admin/jobs/runs")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<JobRun>> getJobRuns() {
        return ResponseEntity.ok(jobRunRepository.findTop20ByOrderByStartedAtDesc());
    }
}
