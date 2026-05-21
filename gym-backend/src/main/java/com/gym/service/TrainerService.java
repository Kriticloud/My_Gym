package com.gym.service;

import com.gym.dto.TrainerDTO;
import com.gym.entity.Trainer;
import com.gym.repository.TrainerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrainerService {

    private final TrainerRepository trainerRepository;

    @Transactional(readOnly = true)
    public List<TrainerDTO> getAllTrainers() {
        return trainerRepository.findByActiveTrue().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TrainerDTO getTrainerById(Long id) {
        Trainer trainer = trainerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trainer not found with id: " + id));
        return toDTO(trainer);
    }

    @Transactional
    public TrainerDTO createTrainer(TrainerDTO dto) {
        Trainer trainer = new Trainer();
        mapDtoToEntity(dto, trainer);
        trainer.setActive(true);
        return toDTO(trainerRepository.save(trainer));
    }

    @Transactional
    public TrainerDTO updateTrainer(Long id, TrainerDTO dto) {
        Trainer trainer = trainerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trainer not found with id: " + id));
        mapDtoToEntity(dto, trainer);
        return toDTO(trainerRepository.save(trainer));
    }

    @Transactional
    public void deleteTrainer(Long id) {
        Trainer trainer = trainerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trainer not found with id: " + id));
        trainer.setActive(false);
        trainerRepository.save(trainer);
    }

    @Transactional(readOnly = true)
    public List<TrainerDTO> getTrainersWithWorkload() {
        return trainerRepository.findTrainersWithMemberCount().stream()
                .map(row -> {
                    Trainer trainer = (Trainer) row[0];
                    Long memberCount = (Long) row[1];
                    TrainerDTO dto = toDTO(trainer);
                    dto.setMemberCount(memberCount.intValue());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    private void mapDtoToEntity(TrainerDTO dto, Trainer trainer) {
        trainer.setFirstName(dto.getFirstName());
        trainer.setLastName(dto.getLastName());
        trainer.setEmail(dto.getEmail());
        trainer.setPhone(dto.getPhone());
        trainer.setSpecialization(dto.getSpecialization());
        trainer.setExperienceYears(dto.getExperienceYears());
        trainer.setBio(dto.getBio());
    }

    private TrainerDTO toDTO(Trainer trainer) {
        return TrainerDTO.builder()
                .id(trainer.getId())
                .firstName(trainer.getFirstName())
                .lastName(trainer.getLastName())
                .email(trainer.getEmail())
                .phone(trainer.getPhone())
                .specialization(trainer.getSpecialization())
                .experienceYears(trainer.getExperienceYears())
                .bio(trainer.getBio())
                .active(trainer.getActive())
                .fullName(trainer.getFullName())
                .memberCount(trainer.getMembers() != null ? trainer.getMembers().size() : 0)
                .build();
    }
}
