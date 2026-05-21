package com.gym.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "attendance")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(name = "check_in_time", nullable = false)
    private LocalDateTime checkInTime;

    @Column(name = "check_out_time")
    private LocalDateTime checkOutTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AttendanceStatus status = AttendanceStatus.CHECKED_IN;

    @Enumerated(EnumType.STRING)
    @Column(name = "check_in_method", nullable = false, length = 20)
    private CheckInMethod checkInMethod = CheckInMethod.MANUAL;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (checkInTime == null)
            checkInTime = LocalDateTime.now();
    }

    public enum AttendanceStatus {
        CHECKED_IN, CHECKED_OUT
    }

    public enum CheckInMethod {
        MANUAL, QR_CODE, ID_CARD
    }
}
