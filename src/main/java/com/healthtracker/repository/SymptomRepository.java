package com.healthtracker.repository;

import com.healthtracker.model.Symptom;
import com.healthtracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface SymptomRepository extends JpaRepository<Symptom, Long> {
    List<Symptom> findByUserAndTimestampBetweenOrderByTimestampDesc(User user, LocalDateTime start, LocalDateTime end);
    List<Symptom> findByUserAndNameOrderByTimestampDesc(User user, String name);
    List<Symptom> findByUserOrderByTimestampDesc(User user);
} 