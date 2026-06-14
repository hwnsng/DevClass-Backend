package com.hwnsng.devclass.scheduler;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PopularCourseSnapshotRepository extends JpaRepository<PopularCourseSnapshot, Long> {
    List<PopularCourseSnapshot> findTop10ByOrderBySnapshotAtDescRankOrderAsc();
}
