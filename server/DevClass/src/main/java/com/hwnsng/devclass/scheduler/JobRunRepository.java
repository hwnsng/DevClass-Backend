package com.hwnsng.devclass.scheduler;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface JobRunRepository extends JpaRepository<JobRun, Long> {
    List<JobRun> findTop20ByOrderByStartedAtDesc();
    List<JobRun> findByJobNameOrderByStartedAtDesc(String jobName);
}
