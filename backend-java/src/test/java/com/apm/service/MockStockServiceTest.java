package com.apm.service;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for MockStockService.
 */
class MockStockServiceTest {

    private final MockStockService mockStockService = new MockStockService();

    @Test
    void getCurrentPrice_withValidTicker_returnsPrice() {
        BigDecimal price = mockStockService.getCurrentPrice("AAPL");
        assertNotNull(price);
        assertEquals(new BigDecimal("185.92"), price);
    }

    @Test
    void getCurrentPrice_withNvda_returnsCorrectPrice() {
        BigDecimal price = mockStockService.getCurrentPrice("NVDA");
        assertEquals(new BigDecimal("485.50"), price);
    }

    @Test
    void getCurrentPrice_withLowercase_returnsPrice() {
        BigDecimal price = mockStockService.getCurrentPrice("googl");
        assertEquals(new BigDecimal("140.25"), price);
    }

    @Test
    void getCurrentPrice_withInvalidTicker_throwsException() {
        assertThrows(StockServiceException.class, () -> {
            mockStockService.getCurrentPrice("INVALID");
        });
    }

    @Test
    void setMockPrice_addsNewTicker() {
        mockStockService.setMockPrice("TEST", new BigDecimal("999.99"));
        BigDecimal price = mockStockService.getCurrentPrice("TEST");
        assertEquals(new BigDecimal("999.99"), price);
    }

    @Test
    void getCurrentPrice_msft_returnsCorrectPrice() {
        BigDecimal price = mockStockService.getCurrentPrice("MSFT");
        assertEquals(new BigDecimal("375.00"), price);
    }

    @Test
    void getCurrentPrice_tesla_returnsCorrectPrice() {
        BigDecimal price = mockStockService.getCurrentPrice("TSLA");
        assertEquals(new BigDecimal("245.75"), price);
    }

    @Test
    void getCurrentPrice_amazon_returnsCorrectPrice() {
        BigDecimal price = mockStockService.getCurrentPrice("AMZN");
        assertEquals(new BigDecimal("155.30"), price);
    }
}
