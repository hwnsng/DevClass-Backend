package com.hwnsng.devclass.payment.repository;

import com.hwnsng.devclass.payment.entity.PaymentItem;
import com.hwnsng.devclass.payment.entity.PaymentItemStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentItemRepository extends JpaRepository<PaymentItem, Long> {
    List<PaymentItem> findByPaymentIdAndStatus(Long paymentId, PaymentItemStatus status);

    @EntityGraph(attributePaths = "payment")
    List<PaymentItem> findByCourseIdAndStatus(Long courseId, PaymentItemStatus status);
}
