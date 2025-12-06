package com.xeno.backend.controller;

import com.xeno.backend.repository.OrderRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api/insights")
public class InsightsController {

    @Autowired
    private OrderRepository orderRepository;

    @GetMapping
    public DashboardStats getStats() {
        BigDecimal revenue = orderRepository.sumTotalRevenue();
        long count = orderRepository.count();

        // Handle null if no orders exist yet
        return new DashboardStats(
                revenue != null ? revenue : BigDecimal.ZERO,
                count
        );
    }

    // Simple DTO class for the JSON response
    @Data
    public static class DashboardStats {
        private final BigDecimal totalRevenue;
        private final long orderCount;

        public DashboardStats(BigDecimal totalRevenue, long orderCount) {
            this.totalRevenue = totalRevenue;
            this.orderCount = orderCount;
        }
    }
}