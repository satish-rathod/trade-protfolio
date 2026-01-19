package com.apm.controller;

import com.apm.dto.TradeRequest;
import com.apm.dto.TradeResponse;
import com.apm.model.Trade;
import com.apm.model.TradeType;
import com.apm.service.PortfolioService;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Unit tests for TradeController using plain Mockito (no Spring context).
 */
@ExtendWith(MockitoExtension.class)
class TradeControllerTest {

        @Mock
        private PortfolioService portfolioService;

        @InjectMocks
        private TradeController tradeController;

        @BeforeEach
        void setUp() {
                // Setup if needed
        }

        @Test
        void createTrade_shouldReturnCreatedWithExecutionPrice() {
                // Arrange
                Trade mockTrade = new Trade(
                                UUID.randomUUID(),
                                "NVDA",
                                TradeType.BUY,
                                10,
                                new BigDecimal("485.50"));
                mockTrade.setId(101L);

                // Mock with 6 parameters (including timestamp)
                when(portfolioService.recordTrade(any(), any(), any(), any(), any(), any()))
                                .thenReturn(mockTrade);

                TradeRequest request = new TradeRequest("NVDA", TradeType.BUY, 10, BigDecimal.ZERO);

                // Act
                ResponseEntity<TradeResponse> response = tradeController.createTrade(request);

                // Assert
                assertEquals(HttpStatus.CREATED, response.getStatusCode());
                assertNotNull(response.getBody());
                assertEquals(101L, response.getBody().getId());
                assertEquals("CONFIRMED", response.getBody().getStatus());
                assertEquals(new BigDecimal("485.50"), response.getBody().getExecutionPrice());
        }

        @Test
        void getAllTrades_shouldReturnListOfTrades() {
                // Arrange
                Trade trade1 = new Trade(UUID.randomUUID(), "AAPL", TradeType.BUY, 5, new BigDecimal("185.00"));
                Trade trade2 = new Trade(UUID.randomUUID(), "GOOGL", TradeType.SELL, 3, new BigDecimal("140.00"));

                when(portfolioService.getAllTrades()).thenReturn(Arrays.asList(trade1, trade2));

                // Act
                ResponseEntity<?> response = tradeController.getAllTrades();

                // Assert
                assertEquals(HttpStatus.OK, response.getStatusCode());
                assertNotNull(response.getBody());
        }

        @Test
        void getPortfolio_shouldReturnPortfolioValue() {
                // Arrange
                Map<String, Object> portfolio = new HashMap<>();
                portfolio.put("totalValue", new BigDecimal("5000.00"));
                portfolio.put("currency", "USD");

                when(portfolioService.calculatePortfolioValue(any())).thenReturn(portfolio);

                // Act
                ResponseEntity<Map<String, Object>> response = tradeController.getPortfolio();

                // Assert
                assertEquals(HttpStatus.OK, response.getStatusCode());
                assertNotNull(response.getBody());
                assertEquals(new BigDecimal("5000.00"), response.getBody().get("totalValue"));
        }
}
