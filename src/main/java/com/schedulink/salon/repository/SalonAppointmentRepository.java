package com.schedulink.salon.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.schedulink.salon.entity.SalonAppointment;

import jakarta.persistence.LockModeType;

public interface SalonAppointmentRepository 
        extends JpaRepository<SalonAppointment, Long> {

    @Query("""
        SELECT COUNT(a) > 0 FROM SalonAppointment a
        WHERE a.providerId = :providerId
        AND a.startTime < :endTime
        AND a.endTime > :startTime
    """)
    boolean existsConflict(
            @Param("providerId") Long providerId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT a FROM SalonAppointment a
        WHERE a.providerId = :providerId
        AND a.startTime < :endTime
        AND a.endTime > :startTime
    """)
    List<SalonAppointment> findConflictingAppointments( 
            Long providerId,
            LocalDateTime startTime,
            LocalDateTime endTime
    );
}