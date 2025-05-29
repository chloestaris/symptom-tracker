package com.healthtracker.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalTime;

@Entity
@Data
public class Medication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @NotNull
    private String name;

    private String dosage;

    private String frequency;

    @ElementCollection
    private java.util.List<LocalTime> reminderTimes;

    private String instructions;

    private boolean active = true;

    @Column(length = 1000)
    private String notes;
} 