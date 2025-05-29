package com.healthtracker.service;

import com.healthtracker.model.User;
import com.healthtracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import jakarta.validation.ValidationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService implements UserDetailsService {
    
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.debug("Loading user by username: {}", username);
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        
        return new org.springframework.security.core.userdetails.User(
            user.getUsername(),
            user.getPassword() != null ? user.getPassword() : "",
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }

    @Transactional
    public User createUser(User user) {
        logger.info("Creating user with username: {}", user.getUsername());
        
        if (userRepository.existsByUsername(user.getUsername())) {
            logger.warn("Username already exists: {}", user.getUsername());
            throw new ValidationException("Username already exists");
        }
        if (user.getEmail() != null && userRepository.existsByEmail(user.getEmail())) {
            logger.warn("Email already exists: {}", user.getEmail());
            throw new ValidationException("Email already exists");
        }
        
        // Only encode password if it's a non-OAuth user
        if (user.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        
        try {
            User savedUser = userRepository.save(user);
            logger.info("Successfully created user: {}", savedUser.getUsername());
            return savedUser;
        } catch (Exception e) {
            logger.error("Error creating user: {}", user.getUsername(), e);
            throw e;
        }
    }

    public User processOAuthPostLogin(OAuth2User oauth2User) {
        String githubId = oauth2User.getAttribute("id").toString();
        String email = oauth2User.getAttribute("email");
        String login = oauth2User.getAttribute("login");
        String name = oauth2User.getAttribute("name");

        Optional<User> userOptional = userRepository.findByGithubId(githubId);
        
        if (userOptional.isPresent()) {
            // Update existing user's information
            User existingUser = userOptional.get();
            existingUser.setEmail(email != null ? email : existingUser.getEmail());
            existingUser.setName(name != null ? name : login);
            return userRepository.save(existingUser);
        }

        // Create new user
        User newUser = new User();
        newUser.setGithubId(githubId);
        newUser.setUsername(login);
        newUser.setEmail(email);
        newUser.setName(name != null ? name : login);
        newUser.setPassword(null); // OAuth2 users don't have passwords
        
        return createUser(newUser);
    }

    public User getUserById(Long id) {
        logger.debug("Getting user by ID: {}", id);
        return userRepository.findById(id)
            .orElseThrow(() -> {
                logger.warn("User not found with ID: {}", id);
                return new RuntimeException("User not found");
            });
    }

    public User getUserByUsername(String username) {
        logger.debug("Getting user by username: {}", username);
        return userRepository.findByUsername(username)
            .orElseGet(() -> {
                logger.info("User {} not found, creating new user", username);
                User newUser = new User();
                newUser.setUsername(username);
                newUser.setPassword(null);
                try {
                    return createUser(newUser);
                } catch (Exception e) {
                    logger.error("Failed to create user for {}", username, e);
                    throw new RuntimeException("Failed to create user", e);
                }
            });
    }

    public Optional<User> findByGithubId(String githubId) {
        logger.debug("Finding user by GitHub ID: {}", githubId);
        return userRepository.findByGithubId(githubId);
    }
} 