package com.hwnsng.devclass.subscription.repository;

import com.hwnsng.devclass.subscription.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    boolean existsByUserIdAndInstructorId(Long userId, Long instructorId);
    void deleteByUserIdAndInstructorId(Long userId, Long instructorId);
    List<Subscription> findByInstructorId(Long instructorId);

    @Query("SELECT s.userId FROM Subscription s WHERE s.instructorId = :instructorId")
    List<Long> findUserIdsByInstructorId(Long instructorId);
}
