package com.hwnsng.devclass.payment.repository;

import com.hwnsng.devclass.payment.entity.Refund;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RefundRepository extends JpaRepository<Refund, Long> {
    List<Refund> findByPaymentId(Long paymentId);
    List<Refund> findByUserId(Long userId);
}
