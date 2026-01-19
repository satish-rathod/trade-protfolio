package com.apm.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Unit tests for User entity.
 */
class UserTest {

    @Test
    void constructor_default_setsCreatedAt() {
        User user = new User();
        assertNotNull(user.getCreatedAt());
    }

    @Test
    void constructor_withParams_setsFields() {
        User user = new User("test@example.com", "hashedPassword123");

        assertEquals("test@example.com", user.getEmail());
        assertEquals("hashedPassword123", user.getPasswordHash());
        assertNotNull(user.getCreatedAt());
    }

    @Test
    void setId_updatesId() {
        User user = new User();
        UUID id = UUID.randomUUID();
        user.setId(id);
        assertEquals(id, user.getId());
    }

    @Test
    void setEmail_updatesEmail() {
        User user = new User();
        user.setEmail("new@email.com");
        assertEquals("new@email.com", user.getEmail());
    }

    @Test
    void setPasswordHash_updatesPasswordHash() {
        User user = new User();
        user.setPasswordHash("newHash");
        assertEquals("newHash", user.getPasswordHash());
    }

    @Test
    void setCreatedAt_updatesTimestamp() {
        User user = new User();
        LocalDateTime newTime = LocalDateTime.of(2024, 1, 1, 0, 0);
        user.setCreatedAt(newTime);
        assertEquals(newTime, user.getCreatedAt());
    }
}
