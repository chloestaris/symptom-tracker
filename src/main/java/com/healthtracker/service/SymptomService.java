package com.healthtracker.service;

import com.healthtracker.model.Symptom;
import com.healthtracker.model.User;
import com.healthtracker.repository.SymptomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class SymptomService {
    
    @Autowired
    private SymptomRepository symptomRepository;

    public Symptom logSymptom(Symptom symptom, User user) {
        if (symptom.getTimestamp() == null) {
            symptom.setTimestamp(LocalDateTime.now());
        }
        symptom.setUser(user);
        return symptomRepository.save(symptom);
    }

    public List<Symptom> getSymptomsByDateRange(User user, LocalDateTime start, LocalDateTime end) {
        return symptomRepository.findByUserAndTimestampBetweenOrderByTimestampDesc(user, start, end);
    }

    public List<Symptom> getSymptomsByName(User user, String name) {
        return symptomRepository.findByUserAndNameOrderByTimestampDesc(user, name);
    }

    public Symptom getSymptomById(Long id, User user) {
        return symptomRepository.findById(id)
            .filter(symptom -> symptom.getUser().equals(user))
            .orElseThrow(() -> new RuntimeException("Symptom not found or access denied"));
    }

    public List<Symptom> getAllSymptoms(User user) {
        return symptomRepository.findByUserOrderByTimestampDesc(user);
    }
} 