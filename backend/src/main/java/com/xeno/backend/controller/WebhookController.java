package com.xeno.backend.controller;

import com.xeno.backend.service.WebhookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@RestController
@RequestMapping("/api/webhooks")
public class WebhookController {

    @Autowired private WebhookService webhookService;
    private final String SHOPIFY_SECRET = "your_shared_secret"; // Move to properties in real app

    @PostMapping("/orders/create")
    public ResponseEntity<Void> handleOrder(
            @RequestHeader("X-Shopify-Shop-Domain") String shopDomain,
            @RequestHeader("X-Shopify-Hmac-Sha256") String hmac,
            @RequestBody String payload) { // Read as String for HMAC verification

        if (!verifyHmac(payload, hmac)) {
            return ResponseEntity.status(401).build();
        }

        // Push to Redis asynchronously
        webhookService.pushToQueue(shopDomain, payload);
        return ResponseEntity.ok().build();
    }

    private boolean verifyHmac(String data, String hmac) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(SHOPIFY_SECRET.getBytes(), "HmacSHA256"));
            byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash).equals(hmac);
        } catch (Exception e) { return false; }
    }
}
