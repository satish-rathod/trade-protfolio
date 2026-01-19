package com.apm.dto;

import java.math.BigDecimal;

/**
 * Data Transfer Object for trade response.
 */
public class TradeResponse {

    private Long id;
    private String status;
    private BigDecimal executionPrice;

    public TradeResponse() {
    }

    public TradeResponse(Long id, String status, BigDecimal executionPrice) {
        this.id = id;
        this.status = status;
        this.executionPrice = executionPrice;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getExecutionPrice() {
        return executionPrice;
    }

    public void setExecutionPrice(BigDecimal executionPrice) {
        this.executionPrice = executionPrice;
    }
}
