package com.healthtracker.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Entity
@Table(name = "users")
@Data
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true)
    @NotBlank(message = "Username is required")
    private String username;
    
    @Email
    @Column(unique = true)
    private String email;
    
    private String password; // Can be null for OAuth2 users
    
    private String name;
    
    @Column(name = "github_id", unique = true)
    private String githubId;
    
    // Constructors, getters, setters...
    
    public String getGithubId() {
        return githubId;
    }
    
    public void setGithubId(String githubId) {
        this.githubId = githubId;
    }
    
    // ... other getters/setters
}