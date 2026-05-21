package com.gym.service;

import com.gym.dto.PaymentDTO;
import com.gym.entity.Member;
import com.gym.entity.MembershipPlan;
import com.gym.entity.Payment;
import com.gym.repository.MemberRepository;
import com.gym.repository.MembershipPlanRepository;
import com.gym.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final MemberRepository memberRepository;
    private final MembershipPlanRepository planRepository;

    @Transactional(readOnly = true)
    public Page<PaymentDTO> getAllPayments(Pageable pageable) {
        return paymentRepository.findAllByOrderByPaymentDateDesc(pageable).map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public PaymentDTO getPaymentById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found with id: " + id));
        return toDTO(payment);
    }

    @Transactional(readOnly = true)
    public List<PaymentDTO> getPaymentsByMember(Long memberId) {
        return paymentRepository.findByMemberId(memberId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public PaymentDTO createPayment(PaymentDTO dto) {
        Member member = memberRepository.findById(dto.getMemberId())
                .orElseThrow(() -> new RuntimeException("Member not found"));

        Payment payment = new Payment();
        payment.setMember(member);
        payment.setAmount(dto.getAmount());
        payment.setPaymentMethod(Payment.PaymentMethod.valueOf(dto.getPaymentMethod()));
        payment.setPaymentStatus(Payment.PaymentStatus.valueOf(
                dto.getPaymentStatus() != null ? dto.getPaymentStatus() : "PAID"));
        payment.setTransactionId(dto.getTransactionId());
        payment.setPaymentDate(LocalDateTime.now());
        payment.setDueDate(dto.getDueDate());
        payment.setNotes(dto.getNotes());

        if (dto.getMembershipPlanId() != null) {
            MembershipPlan plan = planRepository.findById(dto.getMembershipPlanId())
                    .orElseThrow(() -> new RuntimeException("Membership plan not found"));
            payment.setMembershipPlan(plan);

            // Activate membership when payment is made
            if (payment.getPaymentStatus() == Payment.PaymentStatus.PAID) {
                member.setMembershipPlan(plan);
                member.setMembershipStartDate(LocalDate.now());
                member.setMembershipEndDate(LocalDate.now().plusMonths(plan.getDurationMonths()));
                member.setMembershipStatus(Member.MembershipStatus.ACTIVE);
                memberRepository.save(member);
            }
        }

        return toDTO(paymentRepository.save(payment));
    }

    @Transactional
    public PaymentDTO updatePaymentStatus(Long id, String status) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        Payment.PaymentStatus newStatus = Payment.PaymentStatus.valueOf(status);
        payment.setPaymentStatus(newStatus);

        // If payment is marked as paid and it has a plan, activate membership
        if (newStatus == Payment.PaymentStatus.PAID && payment.getMembershipPlan() != null) {
            Member member = payment.getMember();
            MembershipPlan plan = payment.getMembershipPlan();
            member.setMembershipPlan(plan);
            member.setMembershipStartDate(LocalDate.now());
            member.setMembershipEndDate(LocalDate.now().plusMonths(plan.getDurationMonths()));
            member.setMembershipStatus(Member.MembershipStatus.ACTIVE);
            memberRepository.save(member);
        }

        return toDTO(paymentRepository.save(payment));
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalRevenue() {
        return paymentRepository.getTotalRevenue();
    }

    @Transactional(readOnly = true)
    public BigDecimal getMonthlyRevenue() {
        LocalDateTime startOfMonth = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime now = LocalDateTime.now();
        return paymentRepository.getRevenueByDateRange(startOfMonth, now);
    }

    private PaymentDTO toDTO(Payment payment) {
        return PaymentDTO.builder()
                .id(payment.getId())
                .memberId(payment.getMember().getId())
                .memberName(payment.getMember().getFullName())
                .membershipPlanId(payment.getMembershipPlan() != null ? payment.getMembershipPlan().getId() : null)
                .membershipPlanName(payment.getMembershipPlan() != null ? payment.getMembershipPlan().getName() : null)
                .amount(payment.getAmount())
                .paymentMethod(payment.getPaymentMethod().name())
                .paymentStatus(payment.getPaymentStatus().name())
                .transactionId(payment.getTransactionId())
                .paymentDate(payment.getPaymentDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .dueDate(payment.getDueDate())
                .notes(payment.getNotes())
                .build();
    }
}
