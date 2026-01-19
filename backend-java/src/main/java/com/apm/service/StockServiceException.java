package com.apm.service;

/**
 * Exception thrown when stock service operations fail.
 */
public class StockServiceException extends RuntimeException {

    public StockServiceException(String message) {
        super(message);
    }

    public StockServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
