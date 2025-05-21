package com.healthtracker.repository;

import com.healthtracker.model.Medication;
import com.healthtracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MedicationRepository extends JpaRepository<Medication, Long> {
    List<Medication> findByUserAndActiveTrue(User user);
    List<Medication> findByUserAndNameContainingIgnoreCase(User user, String name);
    List<Medication> findByUser(User user);
} 