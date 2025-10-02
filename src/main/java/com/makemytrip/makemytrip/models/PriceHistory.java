package com.makemytrip.makemytrip.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Document(collection = "price_history")
public class PriceHistory {
    @Id
    private String id;
    private String itemId; // flight or hotel ID
    private String itemType; // "flight" or "hotel"
    private List<PricePoint> pricePoints;
    private double currentPrice;
    private double basePrice;
    private double demandMultiplier;
    private LocalDateTime lastUpdated;
    
    @Getter
    @Setter
    public static class PricePoint {
        private LocalDateTime timestamp;
        private double price;
        private String reason; // "demand", "holiday", "weekend", "last-minute"
    }
}