package com.gym.service;

import com.gym.dto.DashboardDTO;
import com.gym.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final MemberRepository memberRepository;
    private final TrainerRepository trainerRepository;
    private final PaymentRepository paymentRepository;
    private final AttendanceRepository attendanceRepository;

    @Transactional(readOnly = true)
    public DashboardDTO getDashboardData() {
        LocalDateTime startOfMonth = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        LocalDateTime now = LocalDateTime.now();

        // Basic counts
        long totalMembers = memberRepository.countTotalActiveMembers();
        long activeMembers = memberRepository.countActiveMembers();
        long totalTrainers = trainerRepository.findByActiveTrue().size();
        BigDecimal totalRevenue = paymentRepository.getTotalRevenue();
        BigDecimal monthlyRevenue = paymentRepository.getRevenueByDateRange(startOfMonth, now);
        long todayAttendance = attendanceRepository.countByDateRange(startOfDay, endOfDay);
        long pendingPayments = paymentRepository.countPendingPayments();

        // Expiring memberships (next 7 days)
        long expiringMemberships = memberRepository
                .findByMembershipEndDateBeforeAndMembershipStatus(
                        LocalDate.now().plusDays(7),
                        com.gym.entity.Member.MembershipStatus.ACTIVE)
                .size();

        // Charts data
        List<Map<String, Object>> membershipGrowth = memberRepository.getMembershipGrowth()
                .stream()
                .map(row -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("month", row[0]);
                    map.put("count", row[1]);
                    return map;
                })
                .collect(Collectors.toList());

        List<Map<String, Object>> revenueChart = paymentRepository.getMonthlyRevenue()
                .stream()
                .map(row -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("month", row[0]);
                    map.put("amount", row[1]);
                    return map;
                })
                .collect(Collectors.toList());

        // Last 30 days attendance
        List<Map<String, Object>> attendanceChart = attendanceRepository
                .getDailyAttendance(LocalDate.now().minusDays(30).atStartOfDay(), now)
                .stream()
                .map(row -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("date", row[0]);
                    map.put("count", row[1]);
                    return map;
                })
                .collect(Collectors.toList());

        // Membership status breakdown
        Map<String, Long> membershipStatusBreakdown = memberRepository.countByMembershipStatus()
                .stream()
                .collect(Collectors.toMap(
                        row -> row[0].toString(),
                        row -> (Long) row[1]));

        return DashboardDTO.builder()
                .totalMembers(totalMembers)
                .activeMembers(activeMembers)
                .totalTrainers(totalTrainers)
                .totalRevenue(totalRevenue != null ? totalRevenue : BigDecimal.ZERO)
                .monthlyRevenue(monthlyRevenue != null ? monthlyRevenue : BigDecimal.ZERO)
                .todayAttendance(todayAttendance)
                .pendingPayments(pendingPayments)
                .expiringMemberships(expiringMemberships)
                .membershipGrowth(membershipGrowth)
                .revenueChart(revenueChart)
                .attendanceChart(attendanceChart)
                .membershipStatusBreakdown(membershipStatusBreakdown)
                .build();
    }
}
