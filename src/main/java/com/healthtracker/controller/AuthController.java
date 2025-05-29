package com.healthtracker.controller;

import com.healthtracker.model.User;
import com.healthtracker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@Valid @RequestBody User user) {
        return ResponseEntity.ok(userService.createUser(user));
    }

    @GetMapping("/user")
    public ResponseEntity<Object> getUser(@AuthenticationPrincipal OAuth2User principal) {
        logger.debug("Getting user info. Principal present: {}", principal != null);
        
        if (principal == null) {
            logger.warn("No authenticated user found");
            return ResponseEntity.status(401).body("Not authenticated");
        }
        
        // Extract user info from GitHub
        String username = principal.getAttribute("login");
        String email = principal.getAttribute("email");
        String name = principal.getAttribute("name");
        
        logger.info("User info retrieved for: {}", username);
        
        return ResponseEntity.ok(Map.of(
            "username", username,
            "email", email != null ? email : "",
            "name", name != null ? name : username
        ));
    }
}