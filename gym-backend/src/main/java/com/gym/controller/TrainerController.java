package com.gym.controller;

import com.gym.dto.ApiResponse;
import com.gym.dto.TrainerDTO;
import com.gym.service.TrainerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trainers")
@RequiredArgsConstructor
public class TrainerController {

    private final TrainerService trainerService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<TrainerDTO>>> getAllTrainers() {
        return ResponseEntity.ok(ApiResponse.success(trainerService.getAllTrainers()));
    }

    @GetMapping("/workload")
    public ResponseEntity<ApiResponse<List<TrainerDTO>>> getTrainersWithWorkload() {
        return ResponseEntity.ok(ApiResponse.success(trainerService.getTrainersWithWorkload()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TrainerDTO>> getTrainerById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(trainerService.getTrainerById(id)));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<TrainerDTO>> createTrainer(@Valid @RequestBody TrainerDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Trainer created successfully", trainerService.createTrainer(dto)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<TrainerDTO>> updateTrainer(
            @PathVariable Long id, @Valid @RequestBody TrainerDTO dto) {
        return ResponseEntity.ok(ApiResponse.success("Trainer updated successfully",
                trainerService.updateTrainer(id, dto)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteTrainer(@PathVariable Long id) {
        trainerService.deleteTrainer(id);
        return ResponseEntity.ok(ApiResponse.success("Trainer deleted successfully", null));
    }
}
