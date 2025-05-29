package com.healthtracker.service;

import com.healthtracker.model.Medication;
import com.healthtracker.model.MedicationLog;
import com.healthtracker.model.User;
import com.healthtracker.repository.MedicationRepository;
import com.healthtracker.repository.MedicationLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MedicationService {
    
    private static final Logger logger = LoggerFactory.getLogger(MedicationService.class);
    
    @Autowired
    private MedicationRepository medicationRepository;
    
    @Autowired
    private MedicationLogRepository medicationLogRepository;

    @Transactional
    public Medication addMedication(Medication medication, User user) {
        if (user == null) {
            logger.error("Attempt to add medication with null user");
            throw new IllegalArgumentException("User cannot be null");
        }
        
        logger.info("Adding medication {} for user {}", medication.getName(), user.getUsername());
        medication.setUser(user);
        Medication saved = medicationRepository.save(medication);
        logger.info("Successfully saved medication with ID: {}", saved.getId());
        return saved;
    }

    public List<Medication> getActiveMedications(User user) {
        logger.info("Getting active medications for user: {}", user.getUsername());
        return medicationRepository.findByUserAndActiveTrue(user);
    }

    @Transactional
    public MedicationLog logMedicationTaken(Long medicationId, User user, boolean taken, String notes) {
        Medication medication = medicationRepository.findById(medicationId)
            .filter(med -> med.getUser().equals(user))
            .orElseThrow(() -> new RuntimeException("Medication not found or access denied"));

        MedicationLog log = new MedicationLog();
        log.setMedication(medication);
        log.setTakenAt(LocalDateTime.now());
        log.setTaken(taken);
        log.setNotes(notes);

        return medicationLogRepository.save(log);
    }

    public List<MedicationLog> getMedicationLogs(Long medicationId, User user, LocalDateTime start, LocalDateTime end) {
        Medication medication = medicationRepository.findById(medicationId)
            .filter(med -> med.getUser().equals(user))
            .orElseThrow(() -> new RuntimeException("Medication not found or access denied"));
            
        return medicationLogRepository.findByMedicationIdAndTakenAtBetween(medicationId, start, end);
    }

    @Transactional
    public Medication updateMedication(Long id, User user, Medication medicationDetails) {
        Medication medication = medicationRepository.findById(id)
            .filter(med -> med.getUser().equals(user))
            .orElseThrow(() -> new RuntimeException("Medication not found or access denied"));
        
        medication.setName(medicationDetails.getName());
        medication.setDosage(medicationDetails.getDosage());
        medication.setFrequency(medicationDetails.getFrequency());
        medication.setReminderTimes(medicationDetails.getReminderTimes());
        medication.setInstructions(medicationDetails.getInstructions());
        medication.setActive(medicationDetails.isActive());
        medication.setNotes(medicationDetails.getNotes());

        return medicationRepository.save(medication);
    }

    public List<Medication> getAllMedications(User user) {
        return medicationRepository.findByUser(user);
    }
} 