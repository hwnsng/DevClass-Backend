package com.hwnsng.devclass.progress.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ProgressTest {

    @Test
    void updateDoesNotDecreaseCompletedPercent() {
        Progress progress = Progress.create(1L, 2L);

        progress.update(100, 3L);
        progress.update(67, 3L);

        assertThat(progress.getPercent()).isEqualTo(100);
        assertThat(progress.getLastLessonId()).isEqualTo(3L);
    }
}
