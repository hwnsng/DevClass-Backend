package com.hwnsng.devclass.external;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:devclass.noreply@gmail.com}")
    private String fromEmail;

    @Value("${app.base-url:http://localhost:3000}")
    private String baseUrl;

    @Async
    public void sendCourseDeletedNotification(String toEmail, String userName,
                                               String courseTitle, String reason) {
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setFrom(fromEmail);
            msg.setTo(toEmail);
            msg.setSubject("[DevClass] 수강 강의 삭제 안내");
            msg.setText(String.format(
                    "%s님 안녕하세요.\n\n" +
                    "수강 중이시던 강의 '%s'가 관리자에 의해 삭제되었습니다.\n\n" +
                    "삭제 사유: %s\n\n" +
                    "불편을 드려 죄송합니다.\n" +
                    "유료 강의였다면 자동 환불 처리됩니다.\n\n" +
                    "DevClass 팀 드림\n%s",
                    userName, courseTitle, reason, baseUrl
            ));
            mailSender.send(msg);
            log.info("Course deleted email sent to {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send course deleted email to {}: {}", toEmail, e.getMessage());
        }
    }

    @Async
    public void sendRefundNotification(String toEmail, String userName,
                                        String courseTitle, int amount) {
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setFrom(fromEmail);
            msg.setTo(toEmail);
            msg.setSubject("[DevClass] 환불 처리 안내");
            msg.setText(String.format(
                    "%s님 안녕하세요.\n\n" +
                    "강의 '%s'에 대한 결제 금액 ₩%s이 환불 처리되었습니다.\n\n" +
                    "환불은 영업일 기준 3~5일 내에 처리됩니다.\n\n" +
                    "DevClass 팀 드림\n%s",
                    userName, courseTitle, String.format("%,d", amount), baseUrl
            ));
            mailSender.send(msg);
            log.info("Refund email sent to {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send refund email to {}: {}", toEmail, e.getMessage());
        }
    }

    @Async
    public void sendNewCourseNotification(String toEmail, String userName,
                                           String courseTitle, Long courseId) {
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setFrom(fromEmail);
            msg.setTo(toEmail);
            msg.setSubject("[DevClass] 새 강의가 등록되었습니다!");
            msg.setText(String.format(
                    "%s님 안녕하세요.\n\n" +
                    "구독 중인 강사님이 새 강의를 등록하였습니다.\n\n" +
                    "강의명: %s\n" +
                    "바로가기: %s/courses/%d\n\n" +
                    "DevClass 팀 드림",
                    userName, courseTitle, baseUrl, courseId
            ));
            mailSender.send(msg);
            log.info("New course email sent to {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send new course email to {}: {}", toEmail, e.getMessage());
        }
    }
}
