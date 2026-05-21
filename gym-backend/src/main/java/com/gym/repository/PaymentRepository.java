package com.gym.repository;

import com.gym.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByMemberId(Long memberId);

    Page<Payment> findByMemberId(Long memberId, Pageable pageable);

    List<Payment> findByPaymentStatus(Payment.PaymentStatus status);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.paymentStatus = 'PAID'")
    BigDecimal getTotalRevenue();

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.paymentStatus = 'PAID' AND p.paymentDate BETWEEN :start AND :end")
    BigDecimal getRevenueByDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT FUNCTION('TO_CHAR', p.paymentDate, 'YYYY-MM') as month, COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.paymentStatus = 'PAID' GROUP BY FUNCTION('TO_CHAR', p.paymentDate, 'YYYY-MM') ORDER BY month")
    List<Object[]> getMonthlyRevenue();

    @Query("SELECT COUNT(p) FROM Payment p WHERE p.paymentStatus = 'PENDING'")
    long countPendingPayments();

    Page<Payment> findAllByOrderByPaymentDateDesc(Pageable pageable);
}
