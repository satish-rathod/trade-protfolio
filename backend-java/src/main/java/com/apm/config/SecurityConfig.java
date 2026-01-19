package com.apm.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration for the application.
 * Allows public access to auth endpoints and health checks.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers("/api/health").permitAll()
                        .requestMatchers("/api/v1/trades/**").permitAll()
                        .requestMatchers("/api/v1/portfolio/**").permitAll()
                        .requestMatchers("/api/v1/analytics/**").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()
                        // All other requests require authentication
                        .anyRequest().authenticated())
                // Allow H2 console frames
                .headers(headers -> headers.frameOptions(frame -> frame.disable()));

        return http.build();
    }
}
