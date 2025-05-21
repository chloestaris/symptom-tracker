package com.healthtracker.repository;

import com.healthtracker.model.MedicationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface MedicationLogRepository extends JpaRepository<MedicationLog, Long> {
    List<MedicationLog> findByMedicationIdAndTakenAtBetween(Long medicationId, LocalDateTime start, LocalDateTime end);
    List<MedicationLog> findByTakenAtBetweenOrderByTakenAtDesc(LocalDateTime start, LocalDateTime end);
} 