package com.gym.controller;

import com.gym.dto.ApiResponse;
import com.gym.dto.MemberDTO;
import com.gym.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<MemberDTO>>> getAllMembers() {
        return ResponseEntity.ok(ApiResponse.success(memberService.getAllMembers()));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<MemberDTO>>> searchMembers(
            @RequestParam(defaultValue = "") String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<MemberDTO> result = memberService.searchMembers(query,
                PageRequest.of(page, size, Sort.by("createdAt").descending()));
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MemberDTO>> getMemberById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(memberService.getMemberById(id)));
    }

    @GetMapping("/qr/{qrCode}")
    public ResponseEntity<ApiResponse<MemberDTO>> getMemberByQrCode(@PathVariable String qrCode) {
        return ResponseEntity.ok(ApiResponse.success(memberService.getMemberByQrCode(qrCode)));
    }

    @GetMapping("/trainer/{trainerId}")
    public ResponseEntity<ApiResponse<List<MemberDTO>>> getMembersByTrainer(@PathVariable Long trainerId) {
        return ResponseEntity.ok(ApiResponse.success(memberService.getMembersByTrainer(trainerId)));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public ResponseEntity<ApiResponse<MemberDTO>> createMember(@Valid @RequestBody MemberDTO dto) {
        MemberDTO created = memberService.createMember(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Member created successfully", created));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public ResponseEntity<ApiResponse<MemberDTO>> updateMember(
            @PathVariable Long id, @Valid @RequestBody MemberDTO dto) {
        return ResponseEntity.ok(ApiResponse.success("Member updated successfully",
                memberService.updateMember(id, dto)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteMember(@PathVariable Long id) {
        memberService.deleteMember(id);
        return ResponseEntity.ok(ApiResponse.success("Member deleted successfully", null));
    }

    @PostMapping("/{memberId}/activate/{planId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public ResponseEntity<ApiResponse<MemberDTO>> activateMembership(
            @PathVariable Long memberId, @PathVariable Long planId) {
        return ResponseEntity.ok(ApiResponse.success("Membership activated",
                memberService.activateMembership(memberId, planId)));
    }
}
