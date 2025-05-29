package com.healthtracker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.session.SessionManagementFilter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.config.Customizer;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Bean
    public SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        return new RegisterSessionAuthenticationStrategy(sessionRegistry());
    }

    private RequestMatcher apiRequestMatcher() {
        return request -> {
            String path = request.getRequestURI();
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            logger.info("API request to " + path + (auth != null ? " from user " + auth.getName() : " (unauthenticated)"));
            return path.startsWith("/api/");
        };
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return new SimpleUrlAuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(HttpServletRequest request, 
                                             HttpServletResponse response,
                                             Authentication authentication) throws java.io.IOException {
                String sessionInfo = request.getSession(false) != null ? 
                    " with session " + request.getSession().getId() : " (no session)";
                logger.info("Authentication success for user " + authentication.getName() + sessionInfo);
                
                if (request.getRequestURI().startsWith("/api/")) {
                    // For API requests, don't redirect
                    response.setStatus(HttpStatus.OK.value());
                } else {
                    // For browser requests, redirect to dashboard
                    try {
                        super.onAuthenticationSuccess(request, response, authentication);
                    } catch (Exception e) {
                        logger.error("Error in authentication success handler", e);
                    }
                }
            }
        };
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .securityContext(context -> context
                .securityContextRepository(securityContextRepository())
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/",
                    "/error",
                    "/login",
                    "/swagger-ui/**",
                    "/v3/api-docs/**",
                    "/swagger-ui.html",
                    "/h2-console/**",
                    "/oauth2/**",
                    "/actuator/health/**"
                ).permitAll()
                .requestMatchers("/api/**").authenticated()
                .anyRequest().authenticated()
            )
            .exceptionHandling(e -> e
                .defaultAuthenticationEntryPointFor(
                    (request, response, authException) -> {
                        logger.info("Authentication failed for request to " + request.getRequestURI() + 
                                  ": " + authException.getMessage());
                        response.sendError(HttpStatus.UNAUTHORIZED.value(), "Authentication required");
                    },
                    apiRequestMatcher()
                )
            )
            .oauth2Login(oauth2 -> oauth2
                .successHandler(authenticationSuccessHandler())
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .sessionAuthenticationStrategy(sessionAuthenticationStrategy())
                .maximumSessions(1)
                .sessionRegistry(sessionRegistry())
            )
            .csrf(csrf -> csrf.disable())
            .headers(headers -> headers.frameOptions().disable());

        return http.build();
    }
}