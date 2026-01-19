package com.apm.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for Trade entity.
 */
class TradeTest {

    @Test
    void constructor_default_setsTimestamp() {
        Trade trade = new Trade();
        assertNotNull(trade.getTimestamp());
    }

    @Test
    void constructor_withParams_setsAllFields() {
        UUID userId = UUID.randomUUID();
        Trade trade = new Trade(userId, "AAPL", TradeType.BUY, 10, new BigDecimal("185.50"));

        assertEquals(userId, trade.getUserId());
        assertEquals("AAPL", trade.getTicker());
        assertEquals(TradeType.BUY, trade.getType());
        assertEquals(10, trade.getQuantity());
        assertEquals(new BigDecimal("185.50"), trade.getPrice());
        assertNotNull(trade.getTimestamp());
    }

    @Test
    void setTicker_convertsToUppercase() {
        Trade trade = new Trade();
        trade.setTicker("aapl");
        assertEquals("AAPL", trade.getTicker());
    }

    @Test
    void setTicker_withNull_handlesNull() {
        Trade trade = new Trade();
        trade.setTicker(null);
        assertNull(trade.getTicker());
    }

    @Test
    void setId_updatesId() {
        Trade trade = new Trade();
        trade.setId(123L);
        assertEquals(123L, trade.getId());
    }

    @Test
    void setType_updatesTradetype() {
        Trade trade = new Trade();
        trade.setType(TradeType.SELL);
        assertEquals(TradeType.SELL, trade.getType());
    }

    @Test
    void setQuantity_updatesQuantity() {
        Trade trade = new Trade();
        trade.setQuantity(50);
        assertEquals(50, trade.getQuantity());
    }

    @Test
    void setPrice_updatesPrice() {
        Trade trade = new Trade();
        trade.setPrice(new BigDecimal("99.99"));
        assertEquals(new BigDecimal("99.99"), trade.getPrice());
    }

    @Test
    void setTimestamp_updatesTimestamp() {
        Trade trade = new Trade();
        LocalDateTime newTime = LocalDateTime.of(2024, 1, 15, 10, 30);
        trade.setTimestamp(newTime);
        assertEquals(newTime, trade.getTimestamp());
    }

    @Test
    void setUserId_updatesUserId() {
        Trade trade = new Trade();
        UUID userId = UUID.randomUUID();
        trade.setUserId(userId);
        assertEquals(userId, trade.getUserId());
    }
}
