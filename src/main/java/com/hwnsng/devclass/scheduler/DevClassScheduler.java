package com.hwnsng.devclass.scheduler;

import com.hwnsng.devclass.course.entity.Course;
import com.hwnsng.devclass.course.repository.CourseRepository;
import com.hwnsng.devclass.external.DiscordWebhookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
@RequiredArgsConstructor
public class DevClassScheduler {

    private final CourseRepository courseRepository;
    private final PopularCourseSnapshotRepository snapshotRepository;
    private final JobRunRepository jobRunRepository;
    private final DiscordWebhookService discordService;

    /**
     * 매주 월요일 오전 2시 - 인기 강의 스냅샷 저장
     */
    @Scheduled(cron = "0 0 2 * * MON")
    @Transactional
    public void aggregatePopularCourses() {
        JobRun run = jobRunRepository.save(JobRun.start("popular-course-aggregation"));
        try {
            List<Course> top10 = courseRepository.findAll(
                    PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "studentCount"))
            ).getContent();

            AtomicInteger rank = new AtomicInteger(1);
            top10.forEach(c -> snapshotRepository.save(
                    PopularCourseSnapshot.of(c.getId(), c.getStudentCount(), rank.getAndIncrement())
            ));

            String msg = String.format("인기 강의 TOP %d 스냅샷 저장 완료", top10.size());
            run.success(msg);
            jobRunRepository.save(run);
            log.info("[Scheduler] popular-course-aggregation: {}", msg);
            discordService.sendSchedulerResult("popular-course-aggregation", "SUCCESS", msg);

        } catch (Exception e) {
            run.fail(e.getMessage());
            jobRunRepository.save(run);
            log.error("[Scheduler] popular-course-aggregation failed: {}", e.getMessage());
            discordService.sendSchedulerResult("popular-course-aggregation", "FAIL", e.getMessage());
        }
    }

    /**
     * 매일 오전 3시 - 오래된 스냅샷 정리 (30일 이상)
     */
    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void cleanupOldSnapshots() {
        JobRun run = jobRunRepository.save(JobRun.start("snapshot-cleanup"));
        try {
            LocalDateTime cutoff = LocalDateTime.now().minusDays(30);
            // 간단한 native query 없이 처리
            List<PopularCourseSnapshot> old = snapshotRepository.findAll().stream()
                    .filter(s -> s.getSnapshotAt().isBefore(cutoff))
                    .toList();
            snapshotRepository.deleteAll(old);

            String msg = String.format("오래된 스냅샷 %d건 삭제 완료", old.size());
            run.success(msg);
            jobRunRepository.save(run);
            log.info("[Scheduler] snapshot-cleanup: {}", msg);

        } catch (Exception e) {
            run.fail(e.getMessage());
            jobRunRepository.save(run);
            log.error("[Scheduler] snapshot-cleanup failed: {}", e.getMessage());
        }
    }
}
