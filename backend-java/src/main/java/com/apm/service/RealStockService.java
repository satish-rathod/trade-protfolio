package com.apm.service;

import java.math.BigDecimal;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

/**
 * Production implementation of StockService that calls the Python Market
 * Engine.
 */
@Service
@Profile("!test")
public class RealStockService implements StockService {

    private static final Logger logger = LoggerFactory.getLogger(RealStockService.class);

    private final RestOperations restOperations;
    private final String marketServiceUrl;

    public RealStockService(
            @Value("${market.service.url:http://localhost:5000}") String marketServiceUrl) {
        this(marketServiceUrl, new RestTemplate());
    }

    // Constructor for testing with injected RestOperations
    public RealStockService(String marketServiceUrl, RestOperations restOperations) {
        this.marketServiceUrl = marketServiceUrl;
        this.restOperations = restOperations;
    }

    @Override
    public BigDecimal getCurrentPrice(String ticker) {
        String url = marketServiceUrl + "/price/" + ticker.toUpperCase();
        logger.info("Fetching price for {} from {}", ticker, url);

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restOperations.getForObject(url, Map.class);

            if (response == null || !response.containsKey("price")) {
                throw new StockServiceException("Invalid response from market service for " + ticker);
            }

            Object priceObj = response.get("price");
            BigDecimal price;

            if (priceObj instanceof Number) {
                price = BigDecimal.valueOf(((Number) priceObj).doubleValue());
            } else {
                price = new BigDecimal(priceObj.toString());
            }

            logger.info("Retrieved price {} for {}", price, ticker);
            return price;

        } catch (RestClientException e) {
            logger.error("Failed to fetch price for {}: {}", ticker, e.getMessage());
            throw new StockServiceException("Market service unavailable for " + ticker, e);
        }
    }
}
