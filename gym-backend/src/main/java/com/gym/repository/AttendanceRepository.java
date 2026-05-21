package com.gym.repository;

import com.gym.entity.Attendance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    List<Attendance> findByMemberId(Long memberId);

    Page<Attendance> findByMemberId(Long memberId, Pageable pageable);

    @Query("SELECT a FROM Attendance a WHERE a.member.id = :memberId AND a.status = 'CHECKED_IN' ORDER BY a.checkInTime DESC")
    Optional<Attendance> findActiveCheckIn(@Param("memberId") Long memberId);

    @Query("SELECT a FROM Attendance a WHERE a.member.id = :memberId AND a.checkInTime BETWEEN :start AND :end")
    List<Attendance> findByMemberIdAndDateRange(
            @Param("memberId") Long memberId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.checkInTime BETWEEN :start AND :end")
    long countByDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT FUNCTION('TO_CHAR', a.checkInTime, 'YYYY-MM-DD') as day, COUNT(a) FROM Attendance a WHERE a.checkInTime BETWEEN :start AND :end GROUP BY FUNCTION('TO_CHAR', a.checkInTime, 'YYYY-MM-DD') ORDER BY day")
    List<Object[]> getDailyAttendance(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT COUNT(DISTINCT a.member.id) FROM Attendance a WHERE a.checkInTime BETWEEN :start AND :end")
    long countUniqueMembers(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    Page<Attendance> findAllByOrderByCheckInTimeDesc(Pageable pageable);
}
