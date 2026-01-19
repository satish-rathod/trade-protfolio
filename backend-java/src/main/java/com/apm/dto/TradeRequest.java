package com.apm.dto;

import com.apm.model.TradeType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for trade creation requests.
 * Supports both real-time and historical/backdated trades.
 */
public class TradeRequest {

    @NotBlank(message = "Ticker symbol is required")
    private String ticker;

    @NotNull(message = "Trade type is required")
    private TradeType type;

    @Min(value = 1, message = "Quantity must be greater than 0")
    private Integer quantity;

    private BigDecimal price = BigDecimal.ZERO;

    /**
     * Optional timestamp for backdated/historical trades.
     * If set to a past date, price lookup will be skipped.
     */
    private LocalDateTime timestamp;

    public TradeRequest() {
    }

    public TradeRequest(String ticker, TradeType type, Integer quantity, BigDecimal price) {
        this.ticker = ticker;
        this.type = type;
        this.quantity = quantity;
        this.price = price != null ? price : BigDecimal.ZERO;
    }

    public TradeRequest(String ticker, TradeType type, Integer quantity,
            BigDecimal price, LocalDateTime timestamp) {
        this(ticker, type, quantity, price);
        this.timestamp = timestamp;
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public TradeType getType() {
        return type;
    }

    public void setType(TradeType type) {
        this.type = type;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Check if this is a backdated/historical trade.
     */
    public boolean isHistoricalTrade() {
        return timestamp != null && timestamp.isBefore(LocalDateTime.now().minusMinutes(5));
    }
}
