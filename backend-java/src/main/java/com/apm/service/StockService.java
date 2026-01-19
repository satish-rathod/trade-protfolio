package com.apm.service;

import java.math.BigDecimal;

/**
 * Interface for stock price fetching service.
 * Allows swapping implementations for Testing vs Production.
 */
public interface StockService {

    /**
     * Get the current price for a stock ticker.
     *
     * @param ticker the stock symbol (e.g., AAPL, NVDA)
     * @return the current price as BigDecimal
     * @throws StockServiceException if the ticker is not found or service is
     *                               unavailable
     */
    BigDecimal getCurrentPrice(String ticker);
}
