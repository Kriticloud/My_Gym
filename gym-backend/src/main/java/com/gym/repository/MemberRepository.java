package com.gym.repository;

import com.gym.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    List<Member> findByActiveTrue();

    Optional<Member> findByQrCode(String qrCode);

    Optional<Member> findByEmail(String email);

    List<Member> findByTrainerId(Long trainerId);

    @Query("SELECT m FROM Member m WHERE m.active = true AND " +
            "(LOWER(m.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(m.lastName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(m.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "m.phone LIKE CONCAT('%', :search, '%'))")
    Page<Member> searchMembers(@Param("search") String search, Pageable pageable);

    @Query("SELECT COUNT(m) FROM Member m WHERE m.membershipStatus = 'ACTIVE'")
    long countActiveMembers();

    @Query("SELECT COUNT(m) FROM Member m WHERE m.active = true")
    long countTotalActiveMembers();

    List<Member> findByMembershipEndDateBeforeAndMembershipStatus(LocalDate date, Member.MembershipStatus status);

    @Query("SELECT m.membershipStatus, COUNT(m) FROM Member m WHERE m.active = true GROUP BY m.membershipStatus")
    List<Object[]> countByMembershipStatus();

    @Query("SELECT FUNCTION('TO_CHAR', m.createdAt, 'YYYY-MM') as month, COUNT(m) FROM Member m WHERE m.active = true GROUP BY FUNCTION('TO_CHAR', m.createdAt, 'YYYY-MM') ORDER BY month")
    List<Object[]> getMembershipGrowth();
}
