package com.apm.controller;

import com.apm.dto.TradeRequest;
import com.apm.dto.TradeResponse;
import com.apm.model.Trade;
import com.apm.service.PortfolioService;
import com.apm.service.StockServiceException;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for trade operations and portfolio management.
 */
@RestController
@RequestMapping("/api/v1")
public class TradeController {

    private final PortfolioService portfolioService;

    // Demo user ID (in production, this would come from authentication)
    private static final UUID DEMO_USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");

    public TradeController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    /**
     * Record a new trade (BUY/SELL).
     * Supports both real-time and historical/backdated trades.
     *
     * @param request the trade details
     * @return trade confirmation with execution price
     */
    @PostMapping("/trades")
    public ResponseEntity<TradeResponse> createTrade(@Valid @RequestBody TradeRequest request) {
        Trade trade = portfolioService.recordTrade(
                DEMO_USER_ID,
                request.getTicker(),
                request.getType(),
                request.getQuantity(),
                request.getPrice(),
                request.getTimestamp());

        TradeResponse response = new TradeResponse(
                trade.getId(),
                request.isHistoricalTrade() ? "HISTORICAL" : "CONFIRMED",
                trade.getPrice());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get all trades.
     *
     * @return list of all trades
     */
    @GetMapping("/trades")
    public ResponseEntity<List<Trade>> getAllTrades() {
        return ResponseEntity.ok(portfolioService.getAllTrades());
    }

    /**
     * Get portfolio summary with current values.
     *
     * @return portfolio holdings and total value
     */
    @GetMapping("/portfolio")
    public ResponseEntity<Map<String, Object>> getPortfolio() {
        Map<String, Object> portfolio = portfolioService.calculatePortfolioValue(DEMO_USER_ID);
        return ResponseEntity.ok(portfolio);
    }

    /**
     * Handle stock service exceptions.
     */
    @ExceptionHandler(StockServiceException.class)
    public ResponseEntity<Map<String, String>> handleStockServiceException(StockServiceException e) {
        Map<String, String> error = new HashMap<>();
        error.put("error", e.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(error);
    }
}
