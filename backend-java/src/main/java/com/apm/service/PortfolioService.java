package com.apm.service;

import com.apm.model.Trade;
import com.apm.model.TradeType;
import com.apm.repository.TradeRepository;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for portfolio business logic and trade orchestration.
 */
@Service
public class PortfolioService {

    private static final Logger logger = LoggerFactory.getLogger(PortfolioService.class);

    private final TradeRepository tradeRepository;
    private final StockService stockService;

    public PortfolioService(TradeRepository tradeRepository, StockService stockService) {
        this.tradeRepository = tradeRepository;
        this.stockService = stockService;
    }

    /**
     * Record a new trade. If price is zero, fetch real-time price from market
     * engine.
     *
     * @param userId   the user making the trade
     * @param ticker   the stock symbol
     * @param type     BUY or SELL
     * @param quantity number of shares
     * @param price    price per share (0 to auto-fetch)
     * @return the saved trade with execution price
     */
    @Transactional
    public Trade recordTrade(UUID userId, String ticker, TradeType type,
            Integer quantity, BigDecimal price) {
        return recordTrade(userId, ticker, type, quantity, price, null);
    }

    /**
     * Record a trade with optional timestamp for historical/backdated trades.
     * If timestamp is in the past, price must be provided (no auto-fetch).
     *
     * @param userId    the user making the trade
     * @param ticker    the stock symbol
     * @param type      BUY or SELL
     * @param quantity  number of shares
     * @param price     price per share
     * @param timestamp optional timestamp for backdated trades
     * @return the saved trade
     */
    @Transactional
    public Trade recordTrade(UUID userId, String ticker, TradeType type,
            Integer quantity, BigDecimal price, java.time.LocalDateTime timestamp) {
        BigDecimal executionPrice = price;
        boolean isHistorical = timestamp != null &&
                timestamp.isBefore(java.time.LocalDateTime.now().minusMinutes(5));

        // Auto-fetch price only for current trades (not historical)
        if (!isHistorical && (price == null || price.compareTo(BigDecimal.ZERO) == 0)) {
            logger.info("Price not provided, fetching real-time price for {}", ticker);
            executionPrice = stockService.getCurrentPrice(ticker);
        } else if (isHistorical && (price == null || price.compareTo(BigDecimal.ZERO) == 0)) {
            throw new IllegalArgumentException(
                    "Price is required for historical trades");
        }

        Trade trade = new Trade(userId, ticker, type, quantity, executionPrice);

        // Set custom timestamp for historical trades
        if (timestamp != null) {
            trade.setTimestamp(timestamp);
            logger.info("Recording historical trade for {} on {}", ticker, timestamp);
        }

        Trade savedTrade = tradeRepository.save(trade);

        logger.info("Trade recorded: {} {} shares of {} at ${}",
                type, quantity, ticker, executionPrice);

        return savedTrade;
    }

    /**
     * Calculate the total portfolio value for a user.
     * Fetches real-time prices and computes (Net Qty * Current Price) for each
     * holding.
     *
     * @param userId the user's UUID
     * @return map containing holdings breakdown and total value
     */
    public Map<String, Object> calculatePortfolioValue(UUID userId) {
        List<Trade> trades = tradeRepository.findByUserId(userId);

        // Calculate net quantity per ticker
        Map<String, Integer> holdings = new HashMap<>();
        for (Trade trade : trades) {
            int multiplier = trade.getType() == TradeType.BUY ? 1 : -1;
            holdings.merge(trade.getTicker(),
                    trade.getQuantity() * multiplier,
                    Integer::sum);
        }

        // Remove zero or negative holdings
        holdings.entrySet().removeIf(entry -> entry.getValue() <= 0);

        // Fetch current prices and calculate values
        Map<String, Object> portfolio = new HashMap<>();
        Map<String, Object> holdingDetails = new HashMap<>();
        BigDecimal totalValue = BigDecimal.ZERO;

        for (Map.Entry<String, Integer> entry : holdings.entrySet()) {
            String ticker = entry.getKey();
            int quantity = entry.getValue();

            try {
                BigDecimal currentPrice = stockService.getCurrentPrice(ticker);
                BigDecimal value = currentPrice.multiply(BigDecimal.valueOf(quantity));
                totalValue = totalValue.add(value);

                Map<String, Object> detail = new HashMap<>();
                detail.put("quantity", quantity);
                detail.put("currentPrice", currentPrice);
                detail.put("value", value);
                holdingDetails.put(ticker, detail);
            } catch (StockServiceException e) {
                logger.warn("Could not fetch price for {}: {}", ticker, e.getMessage());
                Map<String, Object> detail = new HashMap<>();
                detail.put("quantity", quantity);
                detail.put("error", "Price unavailable");
                holdingDetails.put(ticker, detail);
            }
        }

        portfolio.put("holdings", holdingDetails);
        portfolio.put("totalValue", totalValue);
        portfolio.put("currency", "USD");

        return portfolio;
    }

    /**
     * Get all trades for a user.
     *
     * @param userId the user's UUID
     * @return list of trades
     */
    public List<Trade> getTradeHistory(UUID userId) {
        return tradeRepository.findByUserId(userId);
    }

    /**
     * Get all trades (for demo purposes without authentication).
     *
     * @return list of all trades
     */
    public List<Trade> getAllTrades() {
        return tradeRepository.findAll();
    }
}
