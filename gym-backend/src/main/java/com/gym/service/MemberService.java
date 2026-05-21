package com.gym.service;

import com.gym.dto.MemberDTO;
import com.gym.entity.Member;
import com.gym.entity.MembershipPlan;
import com.gym.entity.Trainer;
import com.gym.repository.MemberRepository;
import com.gym.repository.MembershipPlanRepository;
import com.gym.repository.TrainerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final MembershipPlanRepository planRepository;
    private final TrainerRepository trainerRepository;

    @Transactional(readOnly = true)
    public List<MemberDTO> getAllMembers() {
        return memberRepository.findByActiveTrue().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<MemberDTO> searchMembers(String search, Pageable pageable) {
        return memberRepository.searchMembers(search, pageable).map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public MemberDTO getMemberById(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Member not found with id: " + id));
        return toDTO(member);
    }

    @Transactional
    public MemberDTO createMember(MemberDTO dto) {
        Member member = new Member();
        mapDtoToEntity(dto, member);
        member.setQrCode(UUID.randomUUID().toString());
        member.setActive(true);

        if (member.getMembershipPlan() != null && member.getMembershipStatus() == null) {
            member.setMembershipStatus(Member.MembershipStatus.INACTIVE);
        }

        Member saved = memberRepository.save(member);
        return toDTO(saved);
    }

    @Transactional
    public MemberDTO updateMember(Long id, MemberDTO dto) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Member not found with id: " + id));
        mapDtoToEntity(dto, member);
        Member saved = memberRepository.save(member);
        return toDTO(saved);
    }

    @Transactional
    public void deleteMember(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Member not found with id: " + id));
        member.setActive(false);
        memberRepository.save(member);
    }

    @Transactional
    public MemberDTO activateMembership(Long memberId, Long planId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));
        MembershipPlan plan = planRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("Membership plan not found"));

        member.setMembershipPlan(plan);
        member.setMembershipStartDate(LocalDate.now());
        member.setMembershipEndDate(LocalDate.now().plusMonths(plan.getDurationMonths()));
        member.setMembershipStatus(Member.MembershipStatus.ACTIVE);

        return toDTO(memberRepository.save(member));
    }

    @Transactional(readOnly = true)
    public List<MemberDTO> getMembersByTrainer(Long trainerId) {
        return memberRepository.findByTrainerId(trainerId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MemberDTO getMemberByQrCode(String qrCode) {
        Member member = memberRepository.findByQrCode(qrCode)
                .orElseThrow(() -> new RuntimeException("Member not found with QR code"));
        return toDTO(member);
    }

    @Transactional
    public void checkAndExpireMembers() {
        List<Member> expiring = memberRepository
                .findByMembershipEndDateBeforeAndMembershipStatus(LocalDate.now(), Member.MembershipStatus.ACTIVE);
        expiring.forEach(m -> m.setMembershipStatus(Member.MembershipStatus.EXPIRED));
        memberRepository.saveAll(expiring);
    }

    private void mapDtoToEntity(MemberDTO dto, Member member) {
        member.setFirstName(dto.getFirstName());
        member.setLastName(dto.getLastName());
        member.setEmail(dto.getEmail());
        member.setPhone(dto.getPhone());
        member.setGender(Member.Gender.valueOf(dto.getGender()));
        member.setDateOfBirth(dto.getDateOfBirth());
        member.setAddress(dto.getAddress());
        member.setEmergencyContact(dto.getEmergencyContact());
        member.setPhotoUrl(dto.getPhotoUrl());

        if (dto.getMembershipPlanId() != null) {
            MembershipPlan plan = planRepository.findById(dto.getMembershipPlanId())
                    .orElseThrow(() -> new RuntimeException("Plan not found"));
            member.setMembershipPlan(plan);
        }

        if (dto.getTrainerId() != null) {
            Trainer trainer = trainerRepository.findById(dto.getTrainerId())
                    .orElseThrow(() -> new RuntimeException("Trainer not found"));
            member.setTrainer(trainer);
        } else {
            member.setTrainer(null);
        }

        if (dto.getMembershipStatus() != null) {
            member.setMembershipStatus(Member.MembershipStatus.valueOf(dto.getMembershipStatus()));
        }
        if (dto.getMembershipStartDate() != null) {
            member.setMembershipStartDate(dto.getMembershipStartDate());
        }
        if (dto.getMembershipEndDate() != null) {
            member.setMembershipEndDate(dto.getMembershipEndDate());
        }
    }

    public MemberDTO toDTO(Member member) {
        MemberDTO dto = MemberDTO.builder()
                .id(member.getId())
                .firstName(member.getFirstName())
                .lastName(member.getLastName())
                .email(member.getEmail())
                .phone(member.getPhone())
                .gender(member.getGender().name())
                .dateOfBirth(member.getDateOfBirth())
                .address(member.getAddress())
                .emergencyContact(member.getEmergencyContact())
                .photoUrl(member.getPhotoUrl())
                .qrCode(member.getQrCode())
                .membershipStartDate(member.getMembershipStartDate())
                .membershipEndDate(member.getMembershipEndDate())
                .membershipStatus(member.getMembershipStatus().name())
                .active(member.getActive())
                .age(member.getAge())
                .createdAt(member.getCreatedAt() != null
                        ? member.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                        : null)
                .build();

        if (member.getMembershipPlan() != null) {
            dto.setMembershipPlanId(member.getMembershipPlan().getId());
            dto.setMembershipPlanName(member.getMembershipPlan().getName());
        }
        if (member.getTrainer() != null) {
            dto.setTrainerId(member.getTrainer().getId());
            dto.setTrainerName(member.getTrainer().getFullName());
        }
        return dto;
    }
}
