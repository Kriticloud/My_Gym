package com.gym.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardDTO {
    private long totalMembers;
    private long activeMembers;
    private long totalTrainers;
    private BigDecimal totalRevenue;
    private BigDecimal monthlyRevenue;
    private long todayAttendance;
    private long pendingPayments;
    private long expiringMemberships;

    private List<Map<String, Object>> membershipGrowth;
    private List<Map<String, Object>> revenueChart;
    private List<Map<String, Object>> attendanceChart;
    private Map<String, Long> membershipStatusBreakdown;
}
