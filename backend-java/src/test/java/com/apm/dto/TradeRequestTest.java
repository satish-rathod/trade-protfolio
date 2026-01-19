package com.apm.dto;

import com.apm.model.TradeType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Unit tests for TradeRequest DTO.
 */
class TradeRequestTest {

    @Test
    void constructor_default_setsPriceToZero() {
        TradeRequest request = new TradeRequest();
        assertEquals(BigDecimal.ZERO, request.getPrice());
    }

    @Test
    void constructor_withParams_setsAllFields() {
        TradeRequest request = new TradeRequest("AAPL", TradeType.BUY, 10, new BigDecimal("185.50"));

        assertEquals("AAPL", request.getTicker());
        assertEquals(TradeType.BUY, request.getType());
        assertEquals(10, request.getQuantity());
        assertEquals(new BigDecimal("185.50"), request.getPrice());
    }

    @Test
    void constructor_withNullPrice_defaultsToZero() {
        TradeRequest request = new TradeRequest("NVDA", TradeType.SELL, 5, null);
        assertEquals(BigDecimal.ZERO, request.getPrice());
    }

    @Test
    void setTicker_updatesTicker() {
        TradeRequest request = new TradeRequest();
        request.setTicker("GOOGL");
        assertEquals("GOOGL", request.getTicker());
    }

    @Test
    void setType_updatesType() {
        TradeRequest request = new TradeRequest();
        request.setType(TradeType.SELL);
        assertEquals(TradeType.SELL, request.getType());
    }

    @Test
    void setQuantity_updatesQuantity() {
        TradeRequest request = new TradeRequest();
        request.setQuantity(25);
        assertEquals(25, request.getQuantity());
    }

    @Test
    void setPrice_updatesPrice() {
        TradeRequest request = new TradeRequest();
        request.setPrice(new BigDecimal("500.00"));
        assertEquals(new BigDecimal("500.00"), request.getPrice());
    }
}
