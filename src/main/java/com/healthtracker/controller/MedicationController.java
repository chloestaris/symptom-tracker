package com.healthtracker.controller;

import com.healthtracker.model.Medication;
import com.healthtracker.model.MedicationLog;
import com.healthtracker.model.User;
import com.healthtracker.service.MedicationService;
import com.healthtracker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/medications")
public class MedicationController {

    @Autowired
    private MedicationService medicationService;

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<Medication> addMedication(@Valid @RequestBody Medication medication, Authentication auth) {
        User user = userService.getUserByUsername(auth.getName());
        return ResponseEntity.ok(medicationService.addMedication(medication, user));
    }

    @GetMapping("/active")
    public ResponseEntity<List<Medication>> getActiveMedications(Authentication auth) {
        User user = userService.getUserByUsername(auth.getName());
        return ResponseEntity.ok(medicationService.getActiveMedications(user));
    }

    @PostMapping("/{id}/log")
    public ResponseEntity<MedicationLog> logMedicationTaken(
            @PathVariable Long id,
            @RequestParam boolean taken,
            @RequestParam(required = false) String notes,
            Authentication auth) {
        User user = userService.getUserByUsername(auth.getName());
        return ResponseEntity.ok(medicationService.logMedicationTaken(id, user, taken, notes));
    }

    @GetMapping("/{id}/logs")
    public ResponseEntity<List<MedicationLog>> getMedicationLogs(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
            Authentication auth) {
        User user = userService.getUserByUsername(auth.getName());
        return ResponseEntity.ok(medicationService.getMedicationLogs(id, user, start, end));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Medication> updateMedication(
            @PathVariable Long id,
            @Valid @RequestBody Medication medicationDetails,
            Authentication auth) {
        User user = userService.getUserByUsername(auth.getName());
        return ResponseEntity.ok(medicationService.updateMedication(id, user, medicationDetails));
    }

    @GetMapping("/all")
    public ResponseEntity<List<Medication>> getAllMedications(Authentication auth) {
        User user = userService.getUserByUsername(auth.getName());
        return ResponseEntity.ok(medicationService.getAllMedications(user));
    }
} 