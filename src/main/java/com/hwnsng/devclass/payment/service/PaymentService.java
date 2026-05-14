package com.hwnsng.devclass.payment.service;

import com.hwnsng.devclass.cart.service.CartService;
import com.hwnsng.devclass.common.exception.CustomException;
import com.hwnsng.devclass.course.entity.Course;
import com.hwnsng.devclass.course.repository.CourseRepository;
import com.hwnsng.devclass.enrollment.dto.EnrollmentRequest;
import com.hwnsng.devclass.enrollment.service.EnrollmentService;
import com.hwnsng.devclass.payment.dto.*;
import com.hwnsng.devclass.payment.entity.*;
import com.hwnsng.devclass.payment.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentService {

    private final PaymentRepository     paymentRepository;
    private final PaymentItemRepository paymentItemRepository;
    private final RefundRepository      refundRepository;
    private final CourseRepository      courseRepository;
    private final TossPaymentClient     tossPaymentClient;
    private final CartService           cartService;
    private final EnrollmentService     enrollmentService;

    /**
     * 결제 준비: orderId 생성 + 결제할 강의 정보 반환
     */
    public PreparePaymentResponse prepare(PreparePaymentRequest req) {
        List<Course> courses = req.getCourseIds().stream()
                .map(id -> courseRepository.findById(id)
                        .orElseThrow(() -> new CustomException(
                                HttpStatus.NOT_FOUND, "NOT_FOUND", "강의를 찾을 수 없습니다: " + id)))
                .toList();

        int totalAmount = courses.stream().mapToInt(Course::getPrice).sum();
        String orderId  = "ORDER-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();

        String orderName = courses.size() == 1
                ? courses.get(0).getTitle()
                : courses.get(0).getTitle() + " 외 " + (courses.size() - 1) + "개";

        List<PreparePaymentResponse.PaymentCourseItem> items = courses.stream()
                .map(c -> new PreparePaymentResponse.PaymentCourseItem(c.getId(), c.getTitle(), c.getPrice()))
                .toList();

        return new PreparePaymentResponse(orderId, orderName, totalAmount, items);
    }

    /**
     * 결제 승인: 토스 API 호출 → DB 저장 → 수강 등록 → 장바구니 제거
     */
    @Transactional
    public PaymentResponse confirm(ConfirmPaymentRequest req) {
        // 토스 결제 승인
        tossPaymentClient.confirm(req.getPaymentKey(), req.getOrderId(), req.getAmount());

        // 강의 조회
        List<Course> courses = req.getCourseIds().stream()
                .map(id -> courseRepository.findById(id)
                        .orElseThrow(() -> new CustomException(
                                HttpStatus.NOT_FOUND, "NOT_FOUND", "강의를 찾을 수 없습니다.")))
                .toList();

        // Payment 엔티티 저장
        Payment payment = Payment.create(req.getUserId(), req.getAmount(), req.getOrderId());
        payment.confirm(req.getPaymentKey());
        paymentRepository.save(payment);

        // PaymentItem 저장
        for (Course course : courses) {
            PaymentItem item = PaymentItem.create(payment, course.getId(), course.getPrice());
            paymentItemRepository.save(item);
        }

        // 수강 등록 (각 강의)
        for (Course course : courses) {
            try {
                EnrollmentRequest enrollReq = new EnrollmentRequest(req.getUserId(), course.getId());
                enrollmentService.enroll(enrollReq);
            } catch (CustomException e) {
                // 이미 수강 중인 경우 무시
                if (!"ALREADY_ENROLLED".equals(e.getCode())) throw e;
            }
        }

        // 장바구니에서 결제된 강의 제거
        cartService.removeItemsByCourseIds(req.getUserId(), req.getCourseIds());

        return new PaymentResponse(payment);
    }

    /**
     * 결제 내역 조회
     */
    public List<PaymentResponse> getMyPayments(Long userId) {
        return paymentRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream().map(PaymentResponse::new).toList();
    }

    /**
     * 결제 취소/환불 (전체 또는 항목별)
     */
    @Transactional
    public PaymentResponse cancel(Long paymentId, CancelPaymentRequest req) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new CustomException(
                        HttpStatus.NOT_FOUND, "NOT_FOUND", "결제 내역을 찾을 수 없습니다."));

        if (!payment.getUserId().equals(req.getUserId())) {
            throw new CustomException(HttpStatus.FORBIDDEN, "FORBIDDEN", "본인의 결제만 취소할 수 있습니다.");
        }

        RefundType refundType = RefundType.valueOf(
                req.getRefundType() != null ? req.getRefundType() : "USER_REQUEST");

        if (req.getPaymentItemId() != null) {
            // 항목별 취소
            PaymentItem item = paymentItemRepository.findById(req.getPaymentItemId())
                    .orElseThrow(() -> new CustomException(
                            HttpStatus.NOT_FOUND, "NOT_FOUND", "결제 항목을 찾을 수 없습니다."));
            item.refund(req.getReason());

            tossPaymentClient.cancel(payment.getTossPaymentKey(), req.getReason(), item.getAmount());

            Refund refund = Refund.create(paymentId, item.getId(), req.getUserId(),
                    item.getAmount(), req.getReason(), refundType);
            refund.complete();
            refundRepository.save(refund);

            // 남은 ACTIVE 항목 확인
            List<PaymentItem> activeItems = paymentItemRepository
                    .findByPaymentIdAndStatus(paymentId, PaymentItemStatus.ACTIVE);
            if (activeItems.isEmpty()) payment.cancel();
            else payment.partialCancel();
        } else {
            // 전체 취소
            int totalRefund = 0;
            for (PaymentItem item : payment.getItems()) {
                if (item.getStatus() == PaymentItemStatus.ACTIVE) {
                    item.refund(req.getReason());
                    totalRefund += item.getAmount();
                }
            }
            tossPaymentClient.cancel(payment.getTossPaymentKey(), req.getReason(), totalRefund);

            Refund refund = Refund.create(paymentId, null, req.getUserId(),
                    totalRefund, req.getReason(), refundType);
            refund.complete();
            refundRepository.save(refund);
            payment.cancel();
        }

        return new PaymentResponse(payment);
    }
}
