package com.apm.service;

import com.apm.model.User;
import com.apm.repository.UserRepository;
import com.apm.security.JwtUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service for user authentication and management.
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserService(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.jwtUtil = jwtUtil;
    }

    /**
     * Register a new user with BCrypt password hashing.
     *
     * @param email    user's email
     * @param password plain text password
     * @return the created user
     * @throws IllegalArgumentException if email already exists
     */
    @Transactional
    public User registerUser(String email, String password) {
        // Validate email doesn't exist
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already registered");
        }

        // Validate password strength
        validatePassword(password);

        // Hash password with BCrypt
        String hashedPassword = passwordEncoder.encode(password);

        // Create and save user
        User user = new User(email, hashedPassword);
        return userRepository.save(user);
    }

    /**
     * Authenticate user and return JWT token.
     *
     * @param email    user's email
     * @param password plain text password
     * @return JWT token
     * @throws IllegalArgumentException if credentials are invalid
     */
    public String authenticateUser(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        User user = userOpt.get();

        // Verify password with BCrypt
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        // Generate and return JWT token
        return jwtUtil.generateToken(email);
    }

    /**
     * Find user by email.
     *
     * @param email user's email
     * @return optional user
     */
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Validate password strength.
     * Must be at least 8 characters with 1 special character.
     */
    private void validatePassword(String password) {
        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters");
        }

        // Check for at least one special character
        String specialChars = "!@#$%^&*()_+-=[]{}|;':\",./<>?";
        boolean hasSpecial = password.chars()
                .anyMatch(c -> specialChars.indexOf(c) >= 0);

        if (!hasSpecial) {
            throw new IllegalArgumentException("Password must contain at least 1 special character");
        }
    }
}
