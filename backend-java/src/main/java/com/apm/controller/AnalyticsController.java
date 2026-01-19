package com.apm.controller;

import com.apm.service.AnalyticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

/**
 * REST controller for portfolio analytics and P&L.
 */
@RestController
@RequestMapping("/api/v1/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    // Demo user ID (in production, this would come from JWT token)
    private static final UUID DEMO_USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    /**
     * Get comprehensive portfolio analytics with P&L breakdown.
     *
     * @return portfolio analytics including cost basis, current value, and P&L
     */
    @GetMapping("/portfolio")
    public ResponseEntity<Map<String, Object>> getPortfolioAnalytics() {
        Map<String, Object> analytics = analyticsService.calculatePortfolioAnalytics(DEMO_USER_ID);
        return ResponseEntity.ok(analytics);
    }
}
