package com.apm.dto;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Unit tests for TradeResponse DTO.
 */
class TradeResponseTest {

    @Test
    void constructor_default_fieldsAreNull() {
        TradeResponse response = new TradeResponse();
        assertNull(response.getId());
        assertNull(response.getStatus());
        assertNull(response.getExecutionPrice());
    }

    @Test
    void constructor_withParams_setsAllFields() {
        TradeResponse response = new TradeResponse(101L, "CONFIRMED", new BigDecimal("485.50"));

        assertEquals(101L, response.getId());
        assertEquals("CONFIRMED", response.getStatus());
        assertEquals(new BigDecimal("485.50"), response.getExecutionPrice());
    }

    @Test
    void setId_updatesId() {
        TradeResponse response = new TradeResponse();
        response.setId(999L);
        assertEquals(999L, response.getId());
    }

    @Test
    void setStatus_updatesStatus() {
        TradeResponse response = new TradeResponse();
        response.setStatus("PENDING");
        assertEquals("PENDING", response.getStatus());
    }

    @Test
    void setExecutionPrice_updatesPrice() {
        TradeResponse response = new TradeResponse();
        response.setExecutionPrice(new BigDecimal("123.45"));
        assertEquals(new BigDecimal("123.45"), response.getExecutionPrice());
    }
}
