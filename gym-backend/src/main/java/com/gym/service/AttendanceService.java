package com.gym.service;

import com.gym.dto.AttendanceDTO;
import com.gym.entity.Attendance;
import com.gym.entity.Member;
import com.gym.repository.AttendanceRepository;
import com.gym.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public Page<AttendanceDTO> getAllAttendance(Pageable pageable) {
        return attendanceRepository.findAllByOrderByCheckInTimeDesc(pageable).map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public List<AttendanceDTO> getAttendanceByMember(Long memberId) {
        return attendanceRepository.findByMemberId(memberId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public AttendanceDTO checkIn(Long memberId, String method) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        // Validate active membership
        if (!member.isMembershipActive()) {
            throw new RuntimeException("Member does not have an active membership");
        }

        // Check for duplicate check-in today
        Optional<Attendance> activeCheckIn = attendanceRepository.findActiveCheckIn(memberId);
        if (activeCheckIn.isPresent()) {
            throw new RuntimeException("Member already checked in. Please check out first.");
        }

        Attendance attendance = Attendance.builder()
                .member(member)
                .checkInTime(LocalDateTime.now())
                .status(Attendance.AttendanceStatus.CHECKED_IN)
                .checkInMethod(
                        method != null ? Attendance.CheckInMethod.valueOf(method) : Attendance.CheckInMethod.MANUAL)
                .build();

        return toDTO(attendanceRepository.save(attendance));
    }

    @Transactional
    public AttendanceDTO checkInByQrCode(String qrCode) {
        Member member = memberRepository.findByQrCode(qrCode)
                .orElseThrow(() -> new RuntimeException("Invalid QR code"));
        return checkIn(member.getId(), "QR_CODE");
    }

    @Transactional
    public AttendanceDTO checkOut(Long memberId) {
        Attendance attendance = attendanceRepository.findActiveCheckIn(memberId)
                .orElseThrow(() -> new RuntimeException("No active check-in found"));

        attendance.setCheckOutTime(LocalDateTime.now());
        attendance.setStatus(Attendance.AttendanceStatus.CHECKED_OUT);

        return toDTO(attendanceRepository.save(attendance));
    }

    @Transactional(readOnly = true)
    public long getTodayAttendance() {
        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end = start.plusDays(1);
        return attendanceRepository.countByDateRange(start, end);
    }

    @Transactional(readOnly = true)
    public List<AttendanceDTO> getAttendanceByDateRange(Long memberId, LocalDate from, LocalDate to) {
        return attendanceRepository.findByMemberIdAndDateRange(
                memberId, from.atStartOfDay(), to.plusDays(1).atStartOfDay())
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private AttendanceDTO toDTO(Attendance attendance) {
        return AttendanceDTO.builder()
                .id(attendance.getId())
                .memberId(attendance.getMember().getId())
                .memberName(attendance.getMember().getFullName())
                .checkInTime(attendance.getCheckInTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .checkOutTime(attendance.getCheckOutTime() != null
                        ? attendance.getCheckOutTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                        : null)
                .status(attendance.getStatus().name())
                .checkInMethod(attendance.getCheckInMethod().name())
                .membershipStatus(attendance.getMember().getMembershipStatus().name())
                .build();
    }
}
