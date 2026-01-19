package com.apm.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for RealStockService.
 * Uses a manual stub instead of Mockito to avoid Java 25 mocking issues.
 */
class RealStockServiceTest {

    private StubRestOperations stubRestOperations;
    private RealStockService realStockService;

    @BeforeEach
    void setUp() {
        stubRestOperations = new StubRestOperations();
        realStockService = new RealStockService("http://localhost:5000", stubRestOperations);
    }

    @Test
    void getCurrentPrice_withValidResponse_returnsPrice() {
        Map<String, Object> response = new HashMap<>();
        response.put("ticker", "AAPL");
        response.put("price", 185.92);
        response.put("currency", "USD");

        stubRestOperations.setResponse(response);

        BigDecimal price = realStockService.getCurrentPrice("AAPL");
        assertNotNull(price);
        assertEquals(new BigDecimal("185.92"), price);
    }

    @Test
    void getCurrentPrice_withIntegerPrice_returnsPrice() {
        Map<String, Object> response = new HashMap<>();
        response.put("ticker", "NVDA");
        response.put("price", 485);

        stubRestOperations.setResponse(response);

        BigDecimal price = realStockService.getCurrentPrice("NVDA");
        assertNotNull(price);
    }

    @Test
    void getCurrentPrice_withStringPrice_parsesCorrectly() {
        Map<String, Object> response = new HashMap<>();
        response.put("ticker", "GOOGL");
        response.put("price", "140.25");

        stubRestOperations.setResponse(response);

        BigDecimal price = realStockService.getCurrentPrice("GOOGL");
        assertEquals(new BigDecimal("140.25"), price);
    }

    @Test
    void getCurrentPrice_withNullResponse_throwsException() {
        stubRestOperations.setResponse(null);

        assertThrows(StockServiceException.class, () -> {
            realStockService.getCurrentPrice("AAPL");
        });
    }

    @Test
    void getCurrentPrice_withMissingPrice_throwsException() {
        Map<String, Object> response = new HashMap<>();
        response.put("ticker", "AAPL");
        // Missing 'price' key

        stubRestOperations.setResponse(response);

        assertThrows(StockServiceException.class, () -> {
            realStockService.getCurrentPrice("AAPL");
        });
    }

    @Test
    void getCurrentPrice_withNetworkError_throwsException() {
        stubRestOperations.setException(new RestClientException("Connection refused"));

        assertThrows(StockServiceException.class, () -> {
            realStockService.getCurrentPrice("AAPL");
        });
    }

    /**
     * Stub implementation of RestOperations for testing.
     */
    private static class StubRestOperations implements RestOperations {
        private Object response;
        private RuntimeException exception;

        public void setResponse(Object response) {
            this.response = response;
            this.exception = null;
        }

        public void setException(RuntimeException exception) {
            this.exception = exception;
            this.response = null;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> T getForObject(String url, Class<T> responseType, Object... uriVariables) {
            if (exception != null) {
                throw exception;
            }
            return (T) response;
        }

        // Implement other required methods from RestOperations with empty
        // implementations
        @Override
        public <T> T getForObject(String url, Class<T> responseType, java.util.Map<String, ?> uriVariables) {
            return null;
        }

        @Override
        public <T> T getForObject(java.net.URI url, Class<T> responseType) {
            return null;
        }

        @Override
        public <T> org.springframework.http.ResponseEntity<T> getForEntity(String url, Class<T> responseType,
                Object... uriVariables) {
            return null;
        }

        @Override
        public <T> org.springframework.http.ResponseEntity<T> getForEntity(String url, Class<T> responseType,
                java.util.Map<String, ?> uriVariables) {
            return null;
        }

        @Override
        public <T> org.springframework.http.ResponseEntity<T> getForEntity(java.net.URI url, Class<T> responseType) {
            return null;
        }

        @Override
        public org.springframework.http.HttpHeaders headForHeaders(String url, Object... uriVariables) {
            return null;
        }

        @Override
        public org.springframework.http.HttpHeaders headForHeaders(String url, java.util.Map<String, ?> uriVariables) {
            return null;
        }

        @Override
        public org.springframework.http.HttpHeaders headForHeaders(java.net.URI url) {
            return null;
        }

        @Override
        public java.net.URI postForLocation(String url, Object request, Object... uriVariables) {
            return null;
        }

        @Override
        public java.net.URI postForLocation(String url, Object request, java.util.Map<String, ?> uriVariables) {
            return null;
        }

        @Override
        public java.net.URI postForLocation(java.net.URI url, Object request) {
            return null;
        }

        @Override
        public <T> T postForObject(String url, Object request, Class<T> responseType, Object... uriVariables) {
            return null;
        }

        @Override
        public <T> T postForObject(String url, Object request, Class<T> responseType,
                java.util.Map<String, ?> uriVariables) {
            return null;
        }

        @Override
        public <T> T postForObject(java.net.URI url, Object request, Class<T> responseType) {
            return null;
        }

        @Override
        public <T> org.springframework.http.ResponseEntity<T> postForEntity(String url, Object request,
                Class<T> responseType, Object... uriVariables) {
            return null;
        }

        @Override
        public <T> org.springframework.http.ResponseEntity<T> postForEntity(String url, Object request,
                Class<T> responseType, java.util.Map<String, ?> uriVariables) {
            return null;
        }

        @Override
        public <T> org.springframework.http.ResponseEntity<T> postForEntity(java.net.URI url, Object request,
                Class<T> responseType) {
            return null;
        }

        @Override
        public void put(String url, Object request, Object... uriVariables) {
        }

        @Override
        public void put(String url, Object request, java.util.Map<String, ?> uriVariables) {
        }

        @Override
        public void put(java.net.URI url, Object request) {
        }

        @Override
        public <T> T patchForObject(String url, Object request, Class<T> responseType, Object... uriVariables) {
            return null;
        }

        @Override
        public <T> T patchForObject(String url, Object request, Class<T> responseType,
                java.util.Map<String, ?> uriVariables) {
            return null;
        }

        @Override
        public <T> T patchForObject(java.net.URI url, Object request, Class<T> responseType) {
            return null;
        }

        @Override
        public void delete(String url, Object... uriVariables) {
        }

        @Override
        public void delete(String url, java.util.Map<String, ?> uriVariables) {
        }

        @Override
        public void delete(java.net.URI url) {
        }

        @Override
        public java.util.Set<org.springframework.http.HttpMethod> optionsForAllow(String url, Object... uriVariables) {
            return null;
        }

        @Override
        public java.util.Set<org.springframework.http.HttpMethod> optionsForAllow(String url,
                java.util.Map<String, ?> uriVariables) {
            return null;
        }

        @Override
        public java.util.Set<org.springframework.http.HttpMethod> optionsForAllow(java.net.URI url) {
            return null;
        }

        @Override
        public <T> org.springframework.http.ResponseEntity<T> exchange(String url,
                org.springframework.http.HttpMethod method, org.springframework.http.HttpEntity<?> requestEntity,
                Class<T> responseType, Object... uriVariables) {
            return null;
        }

        @Override
        public <T> org.springframework.http.ResponseEntity<T> exchange(String url,
                org.springframework.http.HttpMethod method, org.springframework.http.HttpEntity<?> requestEntity,
                Class<T> responseType, java.util.Map<String, ?> uriVariables) {
            return null;
        }

        @Override
        public <T> org.springframework.http.ResponseEntity<T> exchange(java.net.URI url,
                org.springframework.http.HttpMethod method, org.springframework.http.HttpEntity<?> requestEntity,
                Class<T> responseType) {
            return null;
        }

        @Override
        public <T> org.springframework.http.ResponseEntity<T> exchange(String url,
                org.springframework.http.HttpMethod method, org.springframework.http.HttpEntity<?> requestEntity,
                org.springframework.core.ParameterizedTypeReference<T> responseType, Object... uriVariables) {
            return null;
        }

        @Override
        public <T> org.springframework.http.ResponseEntity<T> exchange(String url,
                org.springframework.http.HttpMethod method, org.springframework.http.HttpEntity<?> requestEntity,
                org.springframework.core.ParameterizedTypeReference<T> responseType,
                java.util.Map<String, ?> uriVariables) {
            return null;
        }

        @Override
        public <T> org.springframework.http.ResponseEntity<T> exchange(java.net.URI url,
                org.springframework.http.HttpMethod method, org.springframework.http.HttpEntity<?> requestEntity,
                org.springframework.core.ParameterizedTypeReference<T> responseType) {
            return null;
        }

        @Override
        public <T> org.springframework.http.ResponseEntity<T> exchange(
                org.springframework.http.RequestEntity<?> requestEntity, Class<T> responseType) {
            return null;
        }

        @Override
        public <T> org.springframework.http.ResponseEntity<T> exchange(
                org.springframework.http.RequestEntity<?> requestEntity,
                org.springframework.core.ParameterizedTypeReference<T> responseType) {
            return null;
        }

        @Override
        public <T> T execute(String url, org.springframework.http.HttpMethod method,
                org.springframework.web.client.RequestCallback requestCallback,
                org.springframework.web.client.ResponseExtractor<T> responseExtractor, Object... uriVariables) {
            return null;
        }

        @Override
        public <T> T execute(String url, org.springframework.http.HttpMethod method,
                org.springframework.web.client.RequestCallback requestCallback,
                org.springframework.web.client.ResponseExtractor<T> responseExtractor,
                java.util.Map<String, ?> uriVariables) {
            return null;
        }

        @Override
        public <T> T execute(java.net.URI url, org.springframework.http.HttpMethod method,
                org.springframework.web.client.RequestCallback requestCallback,
                org.springframework.web.client.ResponseExtractor<T> responseExtractor) {
            return null;
        }
    }
}
