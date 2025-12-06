package com.xeno.backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xeno.backend.config.TenantContext;
import com.xeno.backend.entity.Order;
import com.xeno.backend.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;

@Service
public class WebhookService {
    @Autowired private StringRedisTemplate redisTemplate;
    @Autowired private OrderRepository orderRepository;
    @Autowired private ObjectMapper mapper;

    // Producer
    public void pushToQueue(String shop, String payload) {
        String message = shop + "||" + payload; // Simple delimiter
        redisTemplate.opsForList().leftPush("order_queue", message);
    }

    // Consumer (Worker)
    @Scheduled(fixedDelay = 500)
    public void processQueue() {
        String message = redisTemplate.opsForList().rightPop("order_queue");
        if (message == null) return;

        try {
            String[] parts = message.split("\\|\\|", 2);
            String shop = parts[0];
            String payload = parts[1];

            TenantContext.setCurrentTenant(shop); // Set context for Hibernate Filter

            JsonNode root = mapper.readTree(payload);
            Order order = new Order();
            order.setShopifyOrderId(root.get("id").asLong());
            order.setTotalPrice(new BigDecimal(root.get("total_price").asText()));
            orderRepository.save(order);

        } catch (Exception e) { e.printStackTrace(); }
        finally { TenantContext.clear(); }
    }
}