package com.hwnsng.devclass.scheduler;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "job_runs")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class JobRun {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "job_name", nullable = false)
    private String jobName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobStatus status = JobStatus.RUNNING;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "finished_at")
    private LocalDateTime finishedAt;

    @PrePersist
    protected void onCreate() { startedAt = LocalDateTime.now(); }

    public static JobRun start(String jobName) {
        JobRun r = new JobRun();
        r.jobName = jobName;
        r.startedAt = LocalDateTime.now();
        return r;
    }

    public void success(String message) {
        this.status = JobStatus.SUCCESS;
        this.message = message;
        this.finishedAt = LocalDateTime.now();
    }

    public void fail(String message) {
        this.status = JobStatus.FAIL;
        this.message = message;
        this.finishedAt = LocalDateTime.now();
    }

    public enum JobStatus { RUNNING, SUCCESS, FAIL }
}
