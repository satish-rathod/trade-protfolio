package com.apm.service;

import com.apm.model.Trade;
import com.apm.model.TradeType;
import com.apm.repository.TradeRepository;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Unit tests for PortfolioService.
 */
@ExtendWith(MockitoExtension.class)
class PortfolioServiceTest {

    @Mock
    private TradeRepository tradeRepository;

    @Mock
    private StockService stockService;

    private PortfolioService portfolioService;

    private UUID testUserId;

    @BeforeEach
    void setUp() {
        portfolioService = new PortfolioService(tradeRepository, stockService);
        testUserId = UUID.randomUUID();
    }

    @Test
    void recordTrade_withPrice_shouldSaveTradeWithProvidedPrice() {
        // Arrange
        BigDecimal price = new BigDecimal("150.00");
        Trade savedTrade = new Trade(testUserId, "AAPL", TradeType.BUY, 10, price);
        savedTrade.setId(1L);

        when(tradeRepository.save(any(Trade.class))).thenReturn(savedTrade);

        // Act
        Trade result = portfolioService.recordTrade(
                testUserId, "AAPL", TradeType.BUY, 10, price);

        // Assert
        assertNotNull(result);
        assertEquals("AAPL", result.getTicker());
        assertEquals(price, result.getPrice());
    }

    @Test
    void recordTrade_withZeroPrice_shouldFetchRealTimePrice() {
        // Arrange
        BigDecimal fetchedPrice = new BigDecimal("185.92");
        when(stockService.getCurrentPrice("AAPL")).thenReturn(fetchedPrice);

        Trade savedTrade = new Trade(testUserId, "AAPL", TradeType.BUY, 10, fetchedPrice);
        savedTrade.setId(1L);
        when(tradeRepository.save(any(Trade.class))).thenReturn(savedTrade);

        // Act
        Trade result = portfolioService.recordTrade(
                testUserId, "AAPL", TradeType.BUY, 10, BigDecimal.ZERO);

        // Assert
        assertNotNull(result);
        assertEquals(fetchedPrice, result.getPrice());
    }

    @Test
    void calculatePortfolioValue_shouldComputeTotalValue() {
        // Arrange
        Trade buy1 = new Trade(testUserId, "AAPL", TradeType.BUY, 10, new BigDecimal("180.00"));
        Trade buy2 = new Trade(testUserId, "NVDA", TradeType.BUY, 5, new BigDecimal("450.00"));

        when(tradeRepository.findByUserId(testUserId)).thenReturn(Arrays.asList(buy1, buy2));
        when(stockService.getCurrentPrice("AAPL")).thenReturn(new BigDecimal("185.00"));
        when(stockService.getCurrentPrice("NVDA")).thenReturn(new BigDecimal("485.00"));

        // Act
        Map<String, Object> portfolio = portfolioService.calculatePortfolioValue(testUserId);

        // Assert
        assertNotNull(portfolio);
        BigDecimal totalValue = (BigDecimal) portfolio.get("totalValue");
        // AAPL: 10 * 185 = 1850, NVDA: 5 * 485 = 2425, Total = 4275
        assertEquals(new BigDecimal("4275.00"), totalValue);
    }

    @Test
    void calculatePortfolioValue_withBuyAndSell_shouldComputeNetQuantity() {
        // Arrange
        Trade buy = new Trade(testUserId, "AAPL", TradeType.BUY, 10, new BigDecimal("180.00"));
        Trade sell = new Trade(testUserId, "AAPL", TradeType.SELL, 3, new BigDecimal("190.00"));

        when(tradeRepository.findByUserId(testUserId)).thenReturn(Arrays.asList(buy, sell));
        when(stockService.getCurrentPrice("AAPL")).thenReturn(new BigDecimal("200.00"));

        // Act
        Map<String, Object> portfolio = portfolioService.calculatePortfolioValue(testUserId);

        // Assert
        assertNotNull(portfolio);
        BigDecimal totalValue = (BigDecimal) portfolio.get("totalValue");
        // Net AAPL: 10 - 3 = 7, Value: 7 * 200 = 1400
        assertEquals(new BigDecimal("1400.00"), totalValue);
    }

    @Test
    void recordTrade_whenMarketServiceFails_shouldThrowException() {
        // Arrange
        when(stockService.getCurrentPrice("INVALID"))
                .thenThrow(new StockServiceException("Ticker not found"));

        // Act & Assert
        assertThrows(StockServiceException.class, () -> portfolioService.recordTrade(
                testUserId, "INVALID", TradeType.BUY, 10, BigDecimal.ZERO));
    }
}
