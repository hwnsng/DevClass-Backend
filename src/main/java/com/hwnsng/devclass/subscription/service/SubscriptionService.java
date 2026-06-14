package com.hwnsng.devclass.subscription.service;

import com.hwnsng.devclass.common.exception.CustomException;
import com.hwnsng.devclass.external.EmailService;
import com.hwnsng.devclass.notification.service.NotificationService;
import com.hwnsng.devclass.subscription.entity.Subscription;
import com.hwnsng.devclass.subscription.repository.SubscriptionRepository;
import com.hwnsng.devclass.user.entity.User;
import com.hwnsng.devclass.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final EmailService emailService;

    @Transactional
    public void subscribe(Long userId, Long instructorId) {
        if (userId.equals(instructorId)) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "CANNOT_SELF_SUBSCRIBE", "자기 자신을 구독할 수 없습니다.");
        }
        if (subscriptionRepository.existsByUserIdAndInstructorId(userId, instructorId)) {
            throw new CustomException(HttpStatus.CONFLICT, "ALREADY_SUBSCRIBED", "이미 구독 중입니다.");
        }
        subscriptionRepository.save(Subscription.create(userId, instructorId));
    }

    @Transactional
    public void unsubscribe(Long userId, Long instructorId) {
        subscriptionRepository.deleteByUserIdAndInstructorId(userId, instructorId);
    }

    public boolean isSubscribed(Long userId, Long instructorId) {
        return subscriptionRepository.existsByUserIdAndInstructorId(userId, instructorId);
    }

    /**
     * 신규 강의 등록 시 구독자에게 앱 알림과 이메일 발송
     */
    @Async
    @Transactional(readOnly = true)
    public void notifyNewCourse(Long instructorId, Long courseId, String courseTitle) {
        List<Long> subscriberIds = subscriptionRepository.findUserIdsByInstructorId(instructorId);
        if (subscriberIds.isEmpty()) return;

        User instructor = userRepository.findById(instructorId).orElse(null);
        String instructorName = instructor != null ? instructor.getName() : "강사";

        subscriberIds.forEach(userId -> {
            // 앱 내 알림
            notificationService.send(userId, "새 강의 알림",
                    String.format("구독 중인 %s 강사님이 새 강의 '%s'를 등록했습니다!", instructorName, courseTitle));

            // 이메일
            userRepository.findById(userId).ifPresent(user ->
                    emailService.sendNewCourseNotification(user.getEmail(), user.getName(), courseTitle, courseId)
            );
        });


        log.info("New course notification sent to {} subscribers for course '{}'",
                subscriberIds.size(), courseTitle);
    }
}
