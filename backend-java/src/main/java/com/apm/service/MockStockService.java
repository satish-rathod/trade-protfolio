package com.apm.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * Mock implementation of StockService for testing.
 * Returns static prices without network access.
 */
@Service
@Profile("test")
public class MockStockService implements StockService {

    private static final Map<String, BigDecimal> MOCK_PRICES = new HashMap<>();

    static {
        MOCK_PRICES.put("AAPL", new BigDecimal("185.92"));
        MOCK_PRICES.put("NVDA", new BigDecimal("485.50"));
        MOCK_PRICES.put("GOOGL", new BigDecimal("140.25"));
        MOCK_PRICES.put("MSFT", new BigDecimal("375.00"));
        MOCK_PRICES.put("TSLA", new BigDecimal("245.75"));
        MOCK_PRICES.put("AMZN", new BigDecimal("155.30"));
    }

    @Override
    public BigDecimal getCurrentPrice(String ticker) {
        String upperTicker = ticker.toUpperCase();
        if (MOCK_PRICES.containsKey(upperTicker)) {
            return MOCK_PRICES.get(upperTicker);
        }
        throw new StockServiceException("Mock: Ticker '" + ticker + "' not found");
    }

    /**
     * Add a mock price for testing purposes.
     *
     * @param ticker the stock symbol
     * @param price  the mock price
     */
    public void setMockPrice(String ticker, BigDecimal price) {
        MOCK_PRICES.put(ticker.toUpperCase(), price);
    }
}
