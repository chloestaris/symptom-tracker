package com.healthtracker.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
public class Symptom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @NotNull
    private String name;

    @NotNull
    private Integer severity; // 1-10 scale

    private String description;

    @NotNull
    private LocalDateTime timestamp;

    private String bodyLocation;

    @Column(length = 1000)
    private String notes;
} 