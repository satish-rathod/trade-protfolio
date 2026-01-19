package com.apm.service;

import com.apm.model.Trade;
import com.apm.model.TradeType;
import com.apm.repository.TradeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Service for portfolio analytics and P&L calculations.
 */
@Service
public class AnalyticsService {

    private static final Logger logger = LoggerFactory.getLogger(AnalyticsService.class);

    private final TradeRepository tradeRepository;
    private final StockService stockService;

    public AnalyticsService(TradeRepository tradeRepository, StockService stockService) {
        this.tradeRepository = tradeRepository;
        this.stockService = stockService;
    }

    /**
     * Calculate comprehensive portfolio analytics with P&L breakdown.
     *
     * @param userId the user's UUID
     * @return analytics including holdings, cost basis, current value, and P&L
     */
    public Map<String, Object> calculatePortfolioAnalytics(UUID userId) {
        List<Trade> trades = tradeRepository.findByUserId(userId);

        // Calculate holdings per ticker with cost basis
        Map<String, HoldingInfo> holdings = new HashMap<>();

        for (Trade trade : trades) {
            String ticker = trade.getTicker();
            holdings.putIfAbsent(ticker, new HoldingInfo());
            HoldingInfo info = holdings.get(ticker);

            if (trade.getType() == TradeType.BUY) {
                BigDecimal tradeCost = trade.getPrice().multiply(BigDecimal.valueOf(trade.getQuantity()));
                info.quantity += trade.getQuantity();
                info.totalCost = info.totalCost.add(tradeCost);
            } else {
                // SELL reduces quantity but doesn't affect average cost basis
                info.quantity -= trade.getQuantity();
            }
        }

        // Calculate current values and P&L
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> holdingsDetail = new HashMap<>();

        BigDecimal totalCostBasis = BigDecimal.ZERO;
        BigDecimal totalCurrentValue = BigDecimal.ZERO;

        for (Map.Entry<String, HoldingInfo> entry : holdings.entrySet()) {
            String ticker = entry.getKey();
            HoldingInfo info = entry.getValue();

            if (info.quantity <= 0) {
                continue; // Skip sold-out positions
            }

            Map<String, Object> tickerAnalytics = new HashMap<>();
            tickerAnalytics.put("quantity", info.quantity);
            tickerAnalytics.put("costBasis", info.totalCost.setScale(2, RoundingMode.HALF_UP));

            // Calculate average cost per share
            BigDecimal avgCost = info.totalCost.divide(
                    BigDecimal.valueOf(info.quantity), 2, RoundingMode.HALF_UP);
            tickerAnalytics.put("avgCostPerShare", avgCost);

            try {
                // Fetch current price
                BigDecimal currentPrice = stockService.getCurrentPrice(ticker);
                BigDecimal currentValue = currentPrice.multiply(BigDecimal.valueOf(info.quantity));

                tickerAnalytics.put("currentPrice", currentPrice);
                tickerAnalytics.put("currentValue", currentValue.setScale(2, RoundingMode.HALF_UP));

                // Calculate P&L
                BigDecimal profitLoss = currentValue.subtract(info.totalCost);
                tickerAnalytics.put("profitLoss", profitLoss.setScale(2, RoundingMode.HALF_UP));

                // Calculate percentage gain/loss
                BigDecimal percentChange = profitLoss.divide(info.totalCost, 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100));
                tickerAnalytics.put("percentChange", percentChange.setScale(2, RoundingMode.HALF_UP));

                totalCurrentValue = totalCurrentValue.add(currentValue);
                totalCostBasis = totalCostBasis.add(info.totalCost);

            } catch (StockServiceException e) {
                logger.warn("Could not fetch price for {}: {}", ticker, e.getMessage());
                tickerAnalytics.put("currentPrice", "unavailable");
                tickerAnalytics.put("error", e.getMessage());
                totalCostBasis = totalCostBasis.add(info.totalCost);
            }

            holdingsDetail.put(ticker, tickerAnalytics);
        }

        result.put("holdings", holdingsDetail);
        result.put("totalCostBasis", totalCostBasis.setScale(2, RoundingMode.HALF_UP));
        result.put("totalCurrentValue", totalCurrentValue.setScale(2, RoundingMode.HALF_UP));

        // Total P&L
        BigDecimal totalProfitLoss = totalCurrentValue.subtract(totalCostBasis);
        result.put("totalProfitLoss", totalProfitLoss.setScale(2, RoundingMode.HALF_UP));

        // Total percentage change
        if (totalCostBasis.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal totalPercentChange = totalProfitLoss
                    .divide(totalCostBasis, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
            result.put("totalPercentChange", totalPercentChange.setScale(2, RoundingMode.HALF_UP));
        } else {
            result.put("totalPercentChange", BigDecimal.ZERO);
        }

        result.put("currency", "USD");

        return result;
    }

    /**
     * Helper class to track holding information during calculation.
     */
    private static class HoldingInfo {
        int quantity = 0;
        BigDecimal totalCost = BigDecimal.ZERO;
    }
}
