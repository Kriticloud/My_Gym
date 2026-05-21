package com.gym.controller;

import com.gym.dto.ApiResponse;
import com.gym.dto.AttendanceDTO;
import com.gym.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<AttendanceDTO>>> getAllAttendance(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(
                attendanceService.getAllAttendance(PageRequest.of(page, size))));
    }

    @GetMapping("/member/{memberId}")
    public ResponseEntity<ApiResponse<List<AttendanceDTO>>> getAttendanceByMember(
            @PathVariable Long memberId) {
        return ResponseEntity.ok(ApiResponse.success(attendanceService.getAttendanceByMember(memberId)));
    }

    @GetMapping("/member/{memberId}/range")
    public ResponseEntity<ApiResponse<List<AttendanceDTO>>> getAttendanceByDateRange(
            @PathVariable Long memberId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(ApiResponse.success(
                attendanceService.getAttendanceByDateRange(memberId, from, to)));
    }

    @PostMapping("/checkin/{memberId}")
    public ResponseEntity<ApiResponse<AttendanceDTO>> checkIn(
            @PathVariable Long memberId,
            @RequestBody(required = false) Map<String, String> body) {
        String method = body != null ? body.get("method") : "MANUAL";
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Checked in successfully",
                        attendanceService.checkIn(memberId, method)));
    }

    @PostMapping("/checkin/qr")
    public ResponseEntity<ApiResponse<AttendanceDTO>> checkInByQrCode(@RequestBody Map<String, String> body) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Checked in via QR code",
                        attendanceService.checkInByQrCode(body.get("qrCode"))));
    }

    @PostMapping("/checkout/{memberId}")
    public ResponseEntity<ApiResponse<AttendanceDTO>> checkOut(@PathVariable Long memberId) {
        return ResponseEntity.ok(ApiResponse.success("Checked out successfully",
                attendanceService.checkOut(memberId)));
    }

    @GetMapping("/today/count")
    public ResponseEntity<ApiResponse<Long>> getTodayAttendance() {
        return ResponseEntity.ok(ApiResponse.success(attendanceService.getTodayAttendance()));
    }
}
