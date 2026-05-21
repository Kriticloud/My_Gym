package com.gym.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberDTO {
    private Long id;

    @NotBlank(message = "First name is required")
    @Size(max = 50)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 50)
    private String lastName;

    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Phone is required")
    @Size(max = 20)
    private String phone;

    @NotNull(message = "Gender is required")
    private String gender;

    private LocalDate dateOfBirth;
    private String address;
    private String emergencyContact;
    private String photoUrl;
    private String qrCode;

    private Long membershipPlanId;
    private String membershipPlanName;

    private Long trainerId;
    private String trainerName;

    private LocalDate membershipStartDate;
    private LocalDate membershipEndDate;
    private String membershipStatus;
    private Boolean active;
    private Integer age;
    private String createdAt;
}
