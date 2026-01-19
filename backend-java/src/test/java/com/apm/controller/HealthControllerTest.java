package com.apm.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Unit tests for HealthController.
 */
class HealthControllerTest {

    private final HealthController healthController = new HealthController();

    @Test
    void health_returnsUpStatus() {
        ResponseEntity<Map<String, String>> response = healthController.health();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("UP", response.getBody().get("status"));
    }

    @Test
    void health_returnsNonNullBody() {
        ResponseEntity<Map<String, String>> response = healthController.health();
        assertNotNull(response.getBody());
    }
}
