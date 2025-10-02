package com.makemytrip.makemytrip.controller;

import com.makemytrip.makemytrip.models.PriceHistory;
import com.makemytrip.makemytrip.service.PricingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/pricing")
@CrossOrigin(origins = "http://localhost:3000")
public class PricingController {
    
    @Autowired
    private PricingService pricingService;
    
    @PostMapping("/calculate")
    public ResponseEntity<Map<String, Double>> calculateDynamicPrice(@RequestBody Map<String, Object> request) {
        try {
            String itemId = (String) request.get("itemId");
            String itemType = (String) request.get("itemType");
            String travelDateStr = (String) request.get("travelDate");
            
            LocalDateTime travelDate = LocalDateTime.parse(travelDateStr);
            double price = pricingService.calculateDynamicPrice(itemId, itemType, travelDate);
            
            return ResponseEntity.ok(Map.of("dynamicPrice", price));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/history/{itemId}/{itemType}")
    public ResponseEntity<PriceHistory> getPriceHistory(@PathVariable String itemId, @PathVariable String itemType) {
        try {
            PriceHistory history = pricingService.getPriceHistory(itemId, itemType);
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/insights/{itemId}/{itemType}")
    public ResponseEntity<Map<String, Object>> getPriceInsights(@PathVariable String itemId, @PathVariable String itemType) {
        try {
            Map<String, Object> insights = pricingService.getPriceInsights(itemId, itemType);
            return ResponseEntity.ok(insights);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/freeze")
    public ResponseEntity<Map<String, Object>> freezePrice(@RequestBody Map<String, Object> request) {
        try {
            String itemId = (String) request.get("itemId");
            String itemType = (String) request.get("itemType");
            String userId = (String) request.get("userId");
            Integer hours = (Integer) request.get("hours");
            
            boolean success = pricingService.freezePrice(itemId, itemType, userId, hours != null ? hours : 24);
            
            return ResponseEntity.ok(Map.of(
                "success", success,
                "message", success ? "Price frozen successfully" : "Failed to freeze price",
                "expiresIn", hours != null ? hours : 24
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}