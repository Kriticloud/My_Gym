package com.gym.service;

import com.gym.dto.MembershipPlanDTO;
import com.gym.entity.MembershipPlan;
import com.gym.repository.MembershipPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MembershipPlanService {

    private final MembershipPlanRepository planRepository;

    @Transactional(readOnly = true)
    public List<MembershipPlanDTO> getAllPlans() {
        return planRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MembershipPlanDTO> getActivePlans() {
        return planRepository.findByActiveTrue().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MembershipPlanDTO getPlanById(Long id) {
        MembershipPlan plan = planRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Membership plan not found with id: " + id));
        return toDTO(plan);
    }

    @Transactional
    public MembershipPlanDTO createPlan(MembershipPlanDTO dto) {
        MembershipPlan plan = new MembershipPlan();
        mapDtoToEntity(dto, plan);
        plan.setActive(true);
        return toDTO(planRepository.save(plan));
    }

    @Transactional
    public MembershipPlanDTO updatePlan(Long id, MembershipPlanDTO dto) {
        MembershipPlan plan = planRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Membership plan not found with id: " + id));
        mapDtoToEntity(dto, plan);
        return toDTO(planRepository.save(plan));
    }

    @Transactional
    public void deletePlan(Long id) {
        MembershipPlan plan = planRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Membership plan not found with id: " + id));
        plan.setActive(false);
        planRepository.save(plan);
    }

    private void mapDtoToEntity(MembershipPlanDTO dto, MembershipPlan plan) {
        plan.setName(dto.getName());
        plan.setDurationMonths(dto.getDurationMonths());
        plan.setPrice(dto.getPrice());
        plan.setBenefits(dto.getBenefits());
        plan.setPlanType(MembershipPlan.PlanType.valueOf(dto.getPlanType()));
    }

    private MembershipPlanDTO toDTO(MembershipPlan plan) {
        return MembershipPlanDTO.builder()
                .id(plan.getId())
                .name(plan.getName())
                .durationMonths(plan.getDurationMonths())
                .price(plan.getPrice())
                .benefits(plan.getBenefits())
                .planType(plan.getPlanType().name())
                .active(plan.getActive())
                .memberCount(plan.getMembers() != null ? plan.getMembers().size() : 0)
                .build();
    }
}
