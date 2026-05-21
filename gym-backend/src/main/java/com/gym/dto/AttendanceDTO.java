package com.gym.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceDTO {
    private Long id;
    private Long memberId;
    private String memberName;
    private String checkInTime;
    private String checkOutTime;
    private String status;
    private String checkInMethod;
    private String membershipStatus;
}
