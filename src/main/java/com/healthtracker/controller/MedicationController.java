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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

@RestController
@RequestMapping("/api/medications")
public class MedicationController {

    private static final Logger logger = LoggerFactory.getLogger(MedicationController.class);

    @Autowired
    private MedicationService medicationService;

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<Medication> addMedication(@Valid @RequestBody Medication medication, Authentication auth) {
        String username = auth.getName();
        logger.info("Adding medication for user: {}", username);
        
        if (auth instanceof OAuth2AuthenticationToken) {
            logger.info("User authenticated via OAuth2");
        }
        
        User user = userService.getUserByUsername(username);
        logger.info("Retrieved user from database: {}", user.getUsername());
        
        Medication saved = medicationService.addMedication(medication, user);
        logger.info("Successfully saved medication: {}", saved.getName());
        
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/active")
    public ResponseEntity<List<Medication>> getActiveMedications(Authentication auth) {
        String username = auth.getName();
        logger.info("Getting active medications for user: {}", username);
        User user = userService.getUserByUsername(username);
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