package com.hwnsng.devclass.payment.repository;

import com.hwnsng.devclass.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByUserIdOrderByCreatedAtDesc(Long userId);
    Optional<Payment> findByTossOrderId(String tossOrderId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Payment p where p.tossOrderId = :orderId")
    Optional<Payment> findByTossOrderIdForUpdate(@Param("orderId") String orderId);
}
