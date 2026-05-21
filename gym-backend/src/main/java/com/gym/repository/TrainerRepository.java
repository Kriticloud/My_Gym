package com.gym.repository;

import com.gym.entity.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrainerRepository extends JpaRepository<Trainer, Long> {
    List<Trainer> findByActiveTrue();

    Optional<Trainer> findByEmail(String email);

    Optional<Trainer> findByUserId(Long userId);

    @Query("SELECT t FROM Trainer t LEFT JOIN FETCH t.members WHERE t.id = :id")
    Optional<Trainer> findByIdWithMembers(Long id);

    @Query("SELECT t, COUNT(m) as memberCount FROM Trainer t LEFT JOIN t.members m WHERE t.active = true GROUP BY t ORDER BY memberCount DESC")
    List<Object[]> findTrainersWithMemberCount();
}
