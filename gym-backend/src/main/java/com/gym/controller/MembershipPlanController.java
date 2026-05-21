package com.gym.controller;

import com.gym.dto.ApiResponse;
import com.gym.dto.MembershipPlanDTO;
import com.gym.service.MembershipPlanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/plans")
@RequiredArgsConstructor
public class MembershipPlanController {

    private final MembershipPlanService planService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<MembershipPlanDTO>>> getAllPlans() {
        return ResponseEntity.ok(ApiResponse.success(planService.getAllPlans()));
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<MembershipPlanDTO>>> getActivePlans() {
        return ResponseEntity.ok(ApiResponse.success(planService.getActivePlans()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MembershipPlanDTO>> getPlanById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(planService.getPlanById(id)));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<MembershipPlanDTO>> createPlan(@Valid @RequestBody MembershipPlanDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Plan created successfully", planService.createPlan(dto)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<MembershipPlanDTO>> updatePlan(
            @PathVariable Long id, @Valid @RequestBody MembershipPlanDTO dto) {
        return ResponseEntity.ok(ApiResponse.success("Plan updated successfully",
                planService.updatePlan(id, dto)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deletePlan(@PathVariable Long id) {
        planService.deletePlan(id);
        return ResponseEntity.ok(ApiResponse.success("Plan deleted successfully", null));
    }
}
