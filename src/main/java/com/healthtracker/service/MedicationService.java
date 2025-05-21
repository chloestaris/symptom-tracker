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

@Service
public class MedicationService {
    
    @Autowired
    private MedicationRepository medicationRepository;
    
    @Autowired
    private MedicationLogRepository medicationLogRepository;

    public Medication addMedication(Medication medication, User user) {
        medication.setUser(user);
        return medicationRepository.save(medication);
    }

    public List<Medication> getActiveMedications(User user) {
        return medicationRepository.findByUserAndActiveTrue(user);
    }

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