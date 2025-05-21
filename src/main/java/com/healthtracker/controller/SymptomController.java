package com.healthtracker.controller;

import com.healthtracker.model.Symptom;
import com.healthtracker.model.User;
import com.healthtracker.service.SymptomService;
import com.healthtracker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.validation.FieldError;
import java.time.LocalDateTime;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/symptoms")
public class SymptomController {
    private static final Logger logger = LoggerFactory.getLogger(SymptomController.class);

    @Autowired
    private SymptomService symptomService;

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<Symptom> logSymptom(@Valid @RequestBody Symptom symptom, Authentication auth) {
        logger.debug("Received symptom: {}", symptom);
        try {
            User user = userService.getUserByUsername(auth.getName());
            logger.debug("Found user: {}", user);
            Symptom saved = symptomService.logSymptom(symptom, user);
            logger.debug("Saved symptom: {}", saved);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            logger.error("Error logging symptom", e);
            throw e;
        }
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
            logger.error("Validation error - {}: {}", fieldName, errorMessage);
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @GetMapping
    public ResponseEntity<List<Symptom>> getSymptoms(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
            Authentication auth) {
        User user = userService.getUserByUsername(auth.getName());
        return ResponseEntity.ok(symptomService.getSymptomsByDateRange(user, start, end));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Symptom>> searchSymptoms(@RequestParam String name, Authentication auth) {
        User user = userService.getUserByUsername(auth.getName());
        return ResponseEntity.ok(symptomService.getSymptomsByName(user, name));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Symptom> getSymptom(@PathVariable Long id, Authentication auth) {
        User user = userService.getUserByUsername(auth.getName());
        return ResponseEntity.ok(symptomService.getSymptomById(id, user));
    }

    @GetMapping("/all")
    public ResponseEntity<List<Symptom>> getAllSymptoms(Authentication auth) {
        User user = userService.getUserByUsername(auth.getName());
        return ResponseEntity.ok(symptomService.getAllSymptoms(user));
    }
} 